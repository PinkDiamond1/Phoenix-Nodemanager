package app.controller;

import app.config.ApplicationPaths;
import app.entity.Wallet;
import app.repository.WalletRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import crypto.CPXKey;
import crypto.CryptoService;
import message.request.cmd.GetAccountCmd;
import message.request.cmd.SendRawTransactionCmd;
import message.response.ExecResult;
import message.transaction.FixedNumber;
import message.transaction.IProduceTransaction;
import message.transaction.Transaction;
import message.transaction.TxObj;
import message.transaction.payload.OperationType;
import message.transaction.payload.Registration;
import message.transaction.payload.Vote;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.util.*;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.WALLET_PAGE)
public class WalletController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RequestCallerService requestCaller;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    private IProduceTransaction txFactory;

    @Value("${app.core.rpc}")
    private String rpcUrl;

    private Logger log = LoggerFactory.getLogger(WalletController.class);

    @GetMapping
    public String getWalletPage(Model model) {

        final ArrayList<Map<String, Object>> walletList = new ArrayList<>();
        final List<String> addresses = new ArrayList<>();
        walletRepository.findAll().forEach(wallet -> {
            final HashMap<String, Object> result = new HashMap<>();
            result.put("walletAddress", wallet.getAddress());
            addresses.add(wallet.getAddress());
            result.put("balance", "0");
            result.put("nonce", "0");
            try {
                final String responseString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(wallet.getAddress()));
                final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class, responseString);
                result.put("balance", response.getResult().get("balance") != null ? (String) response.getResult().get("balance") : "0");
                result.put("nonce", response.getResult().get("nextNonce") != null ? response.getResult().get("nextNonce") : 0);
                walletList.add(result);
            } catch (Exception e) {
                walletList.add(result);
                log.warn("Wallet request failed");
            }
        });

        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("transaction")
                .find(in("from", addresses))
                .sort(new Document("createdAt", -1))
                .limit(15).iterator();

        final ArrayList<Map<String, Object>> txList = new ArrayList<>();
        cursor.forEachRemaining(document -> {
            final HashMap<String, Object> txEntry = new HashMap<>();
            document.forEach(txEntry::put);
            txList.add(txEntry);
        });

        final MongoCursor<Document> witnessesDoc = mongoClient.getDatabase("apex")
                .getCollection("witnessStatus").find().limit(1).iterator();
        final List<Map> witnessList = witnessesDoc.hasNext() ?
                witnessesDoc.next().getList("witnesses", Map.class) :
                new ArrayList<>();
        final ArrayList<String> witnesses = new ArrayList<>();
        witnessList.forEach(w -> witnesses.add((String)w.get("addr")));

        model.addAttribute("addresses", addresses);
        model.addAttribute("transactions", txList);
        model.addAttribute("wallets", walletList);
        model.addAttribute("witnesses", witnesses);
        model.addAttribute( "mnemonic", CPXKey.generateMnemonic());

        return ApplicationPaths.WALLET_PAGE;

    }

    @PostMapping(params = "action=create")
    public String newWallet(@RequestParam(value = "mnemonic") final String mnemonic,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat) {
        return importWallet(mnemonic, password, repeat);
    }

    @PostMapping(params = "action=deleteWallet")
    public String deleteWallet(@RequestParam(value = "address") final String address) {
        walletRepository.deleteById(address);
        return ApplicationPaths.WALLET_PATH;
    }

    @PostMapping(params = "action=import")
    public String importWallet(@RequestParam(value = "importSecret") final String secret,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat){

        if(password.equals(repeat)) {
            KeyStore keyStore = null;
            try{
                keyStore = cryptoService.generateKeyStoreFromRawString(password, CryptoService.KEY_NAME, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid raw");
            }

            try{
                keyStore = cryptoService.generateKeyStoreFromWif(password, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid wif");
            }

            try{
                keyStore = cryptoService.generateKeyStoreFromMnemonic(password, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid mnemonic");
            }

            if(keyStore != null) {
                saveKeystore(keyStore, password);
            }
        }

        return ApplicationPaths.WALLET_PATH;

    }

    @PostMapping(params = "action=transfer")
    public String transfer(@RequestParam(value = "fromAddress") final String from,
                           @RequestParam(value = "toAddress") final String to,
                           @RequestParam(value = "amount") final double amount,
                           @RequestParam(value = "gasPrice") final double gasPrice,
                           @RequestParam(value = "walletPassword") final String password) {

        final Optional<Wallet> wallet = walletRepository.findById(from);
        try {
            CPXKey.getScriptHashFromCPXAddress(to);
        } catch (Exception e) {
            log.warn("This is not a valid address: " + to);
            return ApplicationPaths.WALLET_PATH;
        }

        wallet.ifPresent(account -> {
            try {
                final ECPrivateKey key = (ECPrivateKey) cryptoService.loadKeyPairFromKeyStore(account.getKeystore(),
                        password, CryptoService.KEY_NAME).getPrivate();
                final String accountString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(from));
                final ExecResult resultAccount = jacksonWriter.getObjectFromString(ExecResult.class, accountString);
                if(resultAccount.isSucceed()) {
                    final long nonce = ((Number)resultAccount.getResult().get("nextNonce")).longValue();
                    final Transaction tx = txFactory.create(TxObj.TRANSFER, key, () -> new byte[0],
                            CPXKey.getScriptHashFromCPXAddress(to), nonce,
                            new FixedNumber(amount, FixedNumber.CPX),
                            new FixedNumber(gasPrice, FixedNumber.KGP),
                            new FixedNumber(500, FixedNumber.KP));
                    final SendRawTransactionCmd cmd = new SendRawTransactionCmd(cryptoService.signBytes(key, tx));
                    requestCaller.postRequest(rpcUrl, cmd);
                }
            } catch (Exception e){
                log.warn("Transfer failed with: " + e.getMessage());
                Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            }
        });

        return ApplicationPaths.WALLET_PATH;

    }

    @PostMapping(params = "action=vote")
    public String vote(@RequestParam(value = "fromAddressVm") final String from,
                       @RequestParam(value = "gasPriceVm") final double gasPrice,
                       @RequestParam(value = "gasLimitVm") final long gasLimit,
                       @RequestParam(value = "walletPasswordVm") final String password,
                       @RequestParam(value = "voteCandidate") final String candidate,
                       @RequestParam(value = "voteAmount") final double votes,
                       @RequestParam(value = "voteType") final String type) {

        final Optional<Wallet> wallet = walletRepository.findById(from);
        wallet.ifPresent(account -> {
            try {
                final ECPrivateKey key = (ECPrivateKey) cryptoService.loadKeyPairFromKeyStore(account.getKeystore(),
                        password, CryptoService.KEY_NAME).getPrivate();
                final String accountString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(from));
                final ExecResult resultAccount = jacksonWriter.getObjectFromString(ExecResult.class, accountString);
                if(resultAccount.isSucceed()) {
                    final long nonce = ((Number)resultAccount.getResult().get("nextNonce")).longValue();
                    final Vote vote = Vote.builder()
                            .amount(new FixedNumber(votes, FixedNumber.CPX))
                            .operationType(type.equals("add") ? OperationType.REGISTER : OperationType.REGISTER_CANCEL)
                            .voterPubKeyHash(CPXKey.getScriptHashFromCPXAddress(candidate))
                            .build();
                    final Transaction tx = txFactory.create(TxObj.VOTE, key, vote, Vote.SCRIPT_HASH, nonce,
                            new FixedNumber(0, FixedNumber.P),
                            new FixedNumber(gasPrice, FixedNumber.KGP),
                            new FixedNumber(gasLimit, FixedNumber.KP));
                    final SendRawTransactionCmd cmd = new SendRawTransactionCmd(cryptoService.signBytes(key, tx));
                    requestCaller.postRequest(rpcUrl, cmd);
                }
            } catch (Exception e){
                log.warn("Vote failed with: " + e.getMessage());
                Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            }
        });

        return ApplicationPaths.WALLET_PATH;

    }

    @PostMapping(params = "action=register")
    public String register(@RequestParam(value = "fromAddressVm") final String from,
                           @RequestParam(value = "gasPriceVm") final double gasPrice,
                           @RequestParam(value = "gasLimitVm") final long gasLimit,
                           @RequestParam(value = "walletPasswordVm") final String password,
                           @RequestParam(value = "registerAddress") final String registerAddress,
                           @RequestParam(value = "registerType") final String type,
                           @RequestParam(value = "company", required = false) final String company,
                           @RequestParam(value = "url", required = false) final String url,
                           @RequestParam(value = "country", required = false) final String country,
                           @RequestParam(value = "location", required = false) final String location,
                           @RequestParam(value = "longitude", required = false) final Integer longitude,
                           @RequestParam(value = "latitude", required = false) final Integer latitude) {

        final Optional<Wallet> wallet = walletRepository.findById(from);
        wallet.ifPresent(account -> {
            try {
                final ECPrivateKey key = (ECPrivateKey) cryptoService.loadKeyPairFromKeyStore(account.getKeystore(),
                        password, CryptoService.KEY_NAME).getPrivate();
                final String accountString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(from));
                final ExecResult resultAccount = jacksonWriter.getObjectFromString(ExecResult.class, accountString);
                if(resultAccount.isSucceed()) {
                    final long nonce = ((Number)resultAccount.getResult().get("nextNonce")).longValue();
                    final Registration registration = Registration.builder()
                            .fromPubKeyHash(CPXKey.getScriptHashFromCPXAddress(registerAddress))
                            .operationType(type.equals("add") ? OperationType.REGISTER : OperationType.REGISTER_CANCEL)
                            .country(country != null ? country : "")
                            .url(url != null ? url : "")
                            .name(company != null ? company : "")
                            .address(location != null ? location : "")
                            .longitude(longitude != null ? longitude : 0)
                            .latitude(latitude != null ? latitude : 0)
                            .voteCounts(new FixedNumber(0.0, FixedNumber.CPX))
                            .register(true)
                            .frozen(false)
                            .genesisWitness(false)
                            .build();
                    final Transaction tx = txFactory.create(TxObj.REGISTER, key, registration, Registration.SCRIPT_HASH, nonce,
                            new FixedNumber(0, FixedNumber.P),
                            new FixedNumber(gasPrice, FixedNumber.KGP),
                            new FixedNumber(gasLimit, FixedNumber.KP));
                    final SendRawTransactionCmd cmd = new SendRawTransactionCmd(cryptoService.signBytes(key, tx));
                    requestCaller.postRequest(rpcUrl, cmd);
                }
            } catch (Exception e){
                log.warn("Registration failed with: " + e.getMessage());
                Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            }
        });

        return ApplicationPaths.WALLET_PATH;

    }


    private void saveKeystore(final KeyStore keyStore, final String password){

        try {
            final String address = CPXKey.getPublicAddressCPX((ECPrivateKey) keyStore.getKey(CryptoService.KEY_NAME, password.toCharArray()));
            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                keyStore.store(out, password.toCharArray());
                final Wallet newWallet = Wallet.builder()
                        .address(address)
                        .keystore(out.toByteArray())
                        .build();
                walletRepository.save(newWallet);
            }
        } catch (Exception e) {
            log.error("Failed to crate wallet: " + e.getMessage());
        }

    }

}
