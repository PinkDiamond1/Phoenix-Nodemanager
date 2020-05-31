package app.service.transaction;

import app.entity.Wallet;
import app.repository.WalletRepository;
import crypto.CryptoService;
import crypto.UInt256;
import message.request.cmd.GetAccountCmd;
import message.request.cmd.SendRawTransactionCmd;
import message.response.ExecResult;
import message.transaction.*;
import message.transaction.payload.Proposal;
import message.transaction.payload.ProposalType;
import message.transaction.payload.ProposalVote;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.interfaces.ECPrivateKey;
import java.util.Optional;
import java.util.stream.Stream;

@Primary
@Service("TransactionLogic")
public class TransactionLogic implements IProposalTx {

    private final Logger log = LoggerFactory.getLogger(TransactionLogic.class);

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private RequestCallerService requestCaller;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private IProduceTransaction txFactory;

    @Value("${app.core.rpc}")
    private String rpcUrl;

    @Override
    public void voteOnProposal(final String producer, final String password,
                               final String proposalID, final boolean value) {
        try {
            final UInt256 proposal = new UInt256();
            proposal.fromString(proposalID);
            final ProposalVote vote = ProposalVote.builder()
                    .version(1)
                    .proposalId(proposal)
                    .vote(value)
                    .build();
            executeDefaultTx(producer, password, vote);
        } catch (Exception e){
            log.warn("Vote failed with: " + e.getMessage());
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
        }
    }

    @Override
    public void createNewProposal(final String producer, final String password, final int type,
                                  final double amount, final long timestamp) {
        try{
            final Proposal proposal = Proposal.builder()
                    .version(1)
                    .activeTime(timestamp)
                    .value(new FixedNumber(amount, FixedNumber.CPX).getBytes())
                    .build();
            switch (type){
                case 1:
                    proposal.setType(ProposalType.BLOCK_AWARD);
                    break;
                case 2:
                    proposal.setType(ProposalType.TX_MIN_GAS);
                    break;
                case 3:
                    proposal.setType(ProposalType.TX_GAS_LIMIT);
                    break;
            }
            executeDefaultTx(producer, password, proposal);
        } catch (Exception e){
            log.warn("Proposal failed with: " + e.getMessage());
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
        }
    }

    Optional<Long> getNextNonceForAddress(final String address) {
        try {
            final String accountString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(address));
            log.info("Get Account was: " + accountString);
            final ExecResult resultAccount = jacksonWriter.getObjectFromString(ExecResult.class, accountString);
            log.info("Result was: " + resultAccount.getResult().toString() + "\nStatus: " + resultAccount.getStatus());
            if(resultAccount.isSucceed()) {
                final long nonce = ((Number) resultAccount.getResult().get("nextNonce")).longValue();
                return Optional.of(nonce);
            }
        } catch (Exception e){
            log.warn("Get Account failed with: " + e.getMessage());
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
        }
        return Optional.empty();
    }

    void executeDefaultTx(final String walletAddress, final String password, ISerialize payload) {
        final Optional<Wallet> wallet = walletRepository.findById(walletAddress);
        wallet.ifPresentOrElse(account -> {
            try {
                final ECPrivateKey key = (ECPrivateKey) cryptoService.loadKeyPairFromKeyStore(account.getKeystore(),
                        password, CryptoService.KEY_NAME).getPrivate();
                log.info("Private key loaded");
                final Optional<Long> nonceOpt = getNextNonceForAddress(walletAddress);
                if(nonceOpt.isPresent()) {
                    final long nonce = nonceOpt.get();
                    log.info("Nonce is " + nonce);
                    final Transaction tx = txFactory.create(TxObj.CALL, key, payload, Proposal.SCRIPT_HASH, nonce,
                            new FixedNumber(0, FixedNumber.P),
                            new FixedNumber(1, FixedNumber.KGP),
                            new FixedNumber(500, FixedNumber.KP));
                    log.info("Executing transaction");
                    final SendRawTransactionCmd cmd = new SendRawTransactionCmd(cryptoService.signBytes(key, tx));
                    final String result = requestCaller.postRequest(rpcUrl, cmd);
                    log.info("Execute result was: " + result);
                }
            } catch (Exception e) {
                log.warn("Execute Tx failed with: " + e.getMessage());
                Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            }
        }, () -> log.warn("Wallet for Producer " + walletAddress + " could not be loaded"));
    }
}
