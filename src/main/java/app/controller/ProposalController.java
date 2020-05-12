package app.controller;

import app.config.ApplicationPaths;
import app.entity.Wallet;
import app.repository.WalletRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import crypto.CryptoService;
import message.request.cmd.GetAccountCmd;
import message.request.cmd.GetAllProposalCmd;
import message.request.cmd.GetAllProposalVotesCmd;
import message.request.cmd.SendRawTransactionCmd;
import message.response.ExecResult;
import message.transaction.FixedNumber;
import message.transaction.IProduceTransaction;
import message.transaction.Transaction;
import message.transaction.TxObj;
import message.transaction.payload.Proposal;
import message.transaction.payload.ProposalType;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.PROPOSAL_PAGE)
public class ProposalController {

    @Autowired
    private MongoClient mongoClient;

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

    private final Logger log = LoggerFactory.getLogger(ProposalController.class);

    @GetMapping
    public String getProposalPage(Model model) {

        final Optional<String> producerAccount = getProducerAccount();
        model.addAttribute("producer", producerAccount.isEmpty() ?
                "No registered Producer found" :
                producerAccount.get());

        model.addAttribute("currentTimestamp", Instant.now().toEpochMilli() + (73 * 3600 * 1000));
        model.addAttribute("proposals", getProposalList().toString());
        model.addAttribute("votes", "votes");

        return ApplicationPaths.PROPOSAL_PAGE;

    }

    @PostMapping(params = "action=new")
    public String newProposal(@RequestParam(value = "producer") final String producer,
                              @RequestParam(value = "password") final String password,
                              @RequestParam(value = "proposalType") final int type,
                              @RequestParam(value = "amount") final double amount,
                              @RequestParam(value = "timestamp") final long timestamp){
        log.info("Producer: " + producer);
        final Optional<Wallet> wallet = walletRepository.findById(producer);
        wallet.ifPresentOrElse(account -> {
            try{
                final ECPrivateKey key = (ECPrivateKey) cryptoService.loadKeyPairFromKeyStore(account.getKeystore(),
                        password, CryptoService.KEY_NAME).getPrivate();
                log.info("Private key loaded");
                final String accountString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(producer));
                log.info("Get producer account was: " + accountString);
                final ExecResult resultAccount = jacksonWriter.getObjectFromString(ExecResult.class, accountString);
                log.info("Result was: " + resultAccount.getResult().toString() + "\nStatus: " + resultAccount.getStatus());
                if(resultAccount.isSucceed()) {
                    final long nonce = ((Number)resultAccount.getResult().get("nextNonce")).longValue();
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
                    final Transaction tx = txFactory.create(TxObj.CALL, key, proposal, Proposal.SCRIPT_HASH, nonce,
                            new FixedNumber(0, FixedNumber.P),
                            new FixedNumber(1, FixedNumber.KGP),
                            new FixedNumber(500, FixedNumber.KP));
                    log.info("Transaction was build");
                    final SendRawTransactionCmd cmd = new SendRawTransactionCmd(cryptoService.signBytes(key, tx));
                    final String result = requestCaller.postRequest(rpcUrl, cmd);
                    log.info("Execute result was: " + result);
                }
            } catch (Exception e){
                log.warn("Proposal failed with: " + e.getMessage());
                Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            }
        }, () -> log.warn("Wallet for Producer " + producer + " could not be loaded"));

        return ApplicationPaths.PROPOSAL_PATH;

    }

    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getProposals() {
        try {
            return requestCaller.postRequest(rpcUrl, new GetAllProposalCmd());
        } catch (Exception e) {
            log.warn("Proposal All failed with: " + e.getMessage());
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            return "{}";
        }
    }

    private Optional<String> getProducerAccount(){

        final List<String> addresses = new ArrayList<>(walletRepository.findAll())
                .stream()
                .map(Wallet::getAddress)
                .collect(Collectors.toList());

        final MongoCursor<Document> witnessesDoc = mongoClient.getDatabase("apex")
                .getCollection("witnessStatus").find().limit(1).iterator();

        final List<Map> witnessList = witnessesDoc.hasNext() ?
                witnessesDoc.next().getList("witnesses", Map.class) :
                new ArrayList<>();

        return witnessList.stream()
                .filter(w -> addresses.contains(w.get("addr")))
                .map(w -> (String) w.get("addr"))
                .findFirst();

    }

    private List<Map<String, String>> getProposalList(){

        try {
            final String allProposalsString = requestCaller.postRequest(rpcUrl, new GetAllProposalCmd());
            final ExecResult allProposalsResult = jacksonWriter.getObjectFromString(ExecResult.class, allProposalsString);
            if(allProposalsResult.isSucceed()){
                final List<JsonElement> proposalList = new ArrayList<>();
                JsonParser.parseString
                        (jacksonWriter.getStringFromRequestObject(allProposalsResult.getResult()))
                        .getAsJsonObject()
                        .get("proposals").getAsJsonArray().iterator()
                        .forEachRemaining(proposalList::add);
                return proposalList.stream()
                        .map(JsonElement::getAsJsonObject)
                        .map(proposal -> {
                            final HashMap<String, String> values = new HashMap<>();
                            proposal.keySet().stream()
                                    .filter(key -> !key.equals("voters"))
                                    .forEach(key -> values.put(key, proposal.get(key).getAsString()));
                            return values;
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
            log.error("Failed to get the Proposal list");
        }

        return new ArrayList<>();

    }

}
