package app.service.query;

import app.entity.Wallet;
import app.repository.WalletRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import message.request.cmd.GetAccountCmd;
import message.request.cmd.GetAllProposalCmd;
import message.request.cmd.GetAllProposalVotesCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.in;

@Primary
@Service("DataQueryLogic")
public class DataQueryLogic implements IQueryProposal, IQueryProducer, IQueryWallet {

    private final Logger log = LoggerFactory.getLogger(DataQueryLogic.class);

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Value("${app.core.rpc}")
    private String rpcUrl;

    @Autowired
    private RequestCallerService requestCaller;

    @Override
    public List<Map<String, String>> getAllActiveProposals() {
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

    @Override
    public HashMap<String, List<Integer>> getAllActiveVotes() {
        final HashMap<String, List<Integer>> result = new HashMap<>();
        try {
            final int witnessNum = mongoClient.getDatabase("apex")
                    .getCollection("witnessStatus").find().limit(1)
                    .iterator().next().getList("witnesses", Map.class)
                    .size();
            final String allVotesString = requestCaller.postRequest(rpcUrl, new GetAllProposalVotesCmd());
            final ExecResult allVotesResult = jacksonWriter.getObjectFromString(ExecResult.class, allVotesString);
            if (allVotesResult.isSucceed()) {
                final List<JsonElement> votesList = new ArrayList<>();
                JsonParser.parseString
                        (jacksonWriter.getStringFromRequestObject(allVotesResult.getResult()))
                        .getAsJsonObject()
                        .get("votes").getAsJsonArray().iterator()
                        .forEachRemaining(votesList::add);
                final Map<String, List<JsonObject>> groupedVotes = votesList.stream()
                        .map(JsonElement::getAsJsonObject)
                        .collect(Collectors.groupingBy(vote -> vote.get("proposalID").getAsString()));
                groupedVotes.keySet().forEach(proposal -> {
                    final Map<Boolean, List<JsonObject>> partitionedMap = groupedVotes.get(proposal).stream()
                            .collect(Collectors.partitioningBy(jsonObject -> jsonObject.get("agree").getAsBoolean()));
                    final int votedYes = partitionedMap.get(true).size();
                    final int votedNo = partitionedMap.get(false).size();
                    final int notVoted = witnessNum - votedNo - votedYes;
                    result.put(proposal, Arrays.asList(votedYes, votedNo, notVoted));
                });
            }
        } catch (Exception e){
            log.error("Could not get Votes for proposals! Is the RPC running?");
            Stream.of(e.getStackTrace()).forEach(s -> log.error(s.toString()));
        }
        return result;
    }

    @Override
    public String getAllProposalsRaw() {
        try {
            return requestCaller.postRequest(rpcUrl, new GetAllProposalCmd());
        } catch (Exception e) {
            log.warn("Proposal All failed with: " + e.getMessage());
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.warn(stackTraceElement.toString()));
            return "{}";
        }
    }

    @Override
    public Optional<String> getProducerAddress() {
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

    @Override
    public List<String> getAllWitnesses() {
        final MongoCursor<Document> witnessesDoc = mongoClient.getDatabase("apex")
                .getCollection("witnessStatus").find().limit(1).iterator();
        final List<Map> witnessList = witnessesDoc.hasNext() ?
                witnessesDoc.next().getList("witnesses", Map.class) :
                new ArrayList<>();
        final ArrayList<String> witnesses = new ArrayList<>();
        witnessList.forEach(w -> witnesses.add((String)w.get("addr")));
        return witnesses;
    }

    @Override
    public boolean isProducer() {
        return getProducerAddress().isPresent();
    }

    @Override
    public List<String> getAllWalletAddress() {
        return walletRepository.findAll().stream()
                .map(Wallet::getAddress)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllWalletMaps() {
        final ArrayList<Map<String, Object>> walletList = new ArrayList<>();
        getAllWalletAddress().forEach(walletAddress -> {
            final HashMap<String, Object> result = new HashMap<>();
            result.put("walletAddress", walletAddress);
            result.put("balance", "0");
            result.put("nonce", "0");
            try {
                final String responseString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(walletAddress));
                final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class, responseString);
                result.put("balance", response.getResult().get("balance") != null ? (String) response.getResult().get("balance") : "0");
                result.put("nonce", response.getResult().get("nextNonce") != null ? response.getResult().get("nextNonce") : 0);
                walletList.add(result);
            } catch (Exception e) {
                walletList.add(result);
                log.warn("Wallet request failed");
            }
        });
        return walletList;
    }

    @Override
    public List<Map<String, Object>> getAllTxForWallets() {
        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("transaction")
                .find(in("from", getAllWalletAddress()))
                .sort(new Document("createdAt", -1))
                .limit(15).iterator();
        final ArrayList<Map<String, Object>> txList = new ArrayList<>();
        cursor.forEachRemaining(document -> {
            final HashMap<String, Object> txEntry = new HashMap<>();
            document.forEach(txEntry::put);
            txList.add(txEntry);
        });
        return txList;
    }
}
