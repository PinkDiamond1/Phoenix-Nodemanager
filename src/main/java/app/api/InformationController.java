package app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import message.request.IRPCMessage;
import message.request.ProducerListType;
import message.request.cmd.GetProducersCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.*;

import static com.mongodb.client.model.Filters.*;


@Controller
@RequestMapping(ApiPaths.API)
public class InformationController {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RequestCallerService requestCaller;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Value("${app.core.rpc}")
    private String rpcUrl;

    private Logger log = LoggerFactory.getLogger(InformationController.class);

    @RequestMapping(value = ApiPaths.LAST_BLOCK, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLastBlock() {

        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("block")
                .find().sort(new Document("height", -1))
                .limit(1).iterator();

        return cursor.hasNext() ? cursor.next().toJson() : "{}";

    }

    @RequestMapping(value = ApiPaths.TPS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTps() {

        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("tps_tensec")
                .find().sort(new Document("timeStamp", -1))
                .limit(20).iterator();

        final ArrayList<String> labelsList = new ArrayList<>();
        final ArrayList<Integer> pointsList = new ArrayList<>();
        cursor.forEachRemaining(entry -> {
            labelsList.add("");
            pointsList.add((int) entry.get("txs"));
        });

        final HashMap<String, Object> dataPoints = new HashMap<>();
        dataPoints.put("labels", labelsList);
        dataPoints.put("values", pointsList);

        try {
            return jacksonWriter.getStringFromRequestObject(dataPoints);
        } catch (JsonProcessingException e) {
            return "{}";
        }

    }

    @RequestMapping(value = ApiPaths.WITNESS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getWitnesses() {

        final int blocksPerHour = 7200;
        final long witnessNum = 21L;
       	final double maxBlocksPerHour = blocksPerHour/ (witnessNum *1.0f);

        final ArrayList<HashMap<String, Object>> responseList = new ArrayList<>();
        final MongoCursor<Document> witnesses = mongoClient.getDatabase("apex")
                .getCollection("witnessStatus").find().limit(1).iterator();

        final MongoCursor<Document> producer = mongoClient.getDatabase("apex")
                .getCollection("block")
                .find().sort(new Document("height", -1))
                .limit(1).iterator();

        final HashMap<String, Long> producerBlocksCount = new HashMap<>();
        final MongoCursor<Document> cursorMiner = mongoClient.getDatabase("apex")
                .getCollection("miner")
                .find().sort(new Document("addr", -1))
                .iterator();

        cursorMiner.forEachRemaining(entry -> {
            producerBlocksCount.put((String) entry.get("addr"), 0L);
        });

        final MongoCursor<Document> minerBlockCount = mongoClient.getDatabase("apex")
           	    .getCollection("block")
           	    .find(gte("timeStamp", new BsonDateTime(Instant.now().toEpochMilli() - 3600000L)))   
           	    .iterator();

        minerBlockCount.forEachRemaining(entry -> {  
            if(entry.get("confirmed").toString().equals("true")) {
        	    long newVal = (Long) producerBlocksCount.get(entry.get("producer")) + 1L;
        	    producerBlocksCount.replace(entry.get("producer").toString(), newVal);
            }
        });

        if(witnesses.hasNext() && producer.hasNext()){
            final String currentProducer = producer.next().getString("producer");
            final List<Map> witnessList = witnesses.next().getList("witnesses", Map.class);
            witnessList.forEach(witness -> {
                final HashMap<String, Object> entry = new HashMap<>();
                final String address = (String) witness.get("addr");
                entry.put("name", witness.get("name"));
                entry.put("addr", witness.get("addr"));
                entry.put("voteCounts", witness.get("voteCounts"));               
                double yield = (producerBlocksCount.containsKey(address) ? producerBlocksCount.get(address) : 0L * 1.0 / maxBlocksPerHour) * 100.0;
                String formattedString = String.format("%.01f", yield) + "%";
                entry.put("yield", formattedString);
                entry.put("longitude", witness.get("longitude"));
                entry.put("latitude", witness.get("latitude"));
                entry.put("radius", address.equals(currentProducer) ? 12 : 4);
                entry.put("fillKey", address.equals(currentProducer) ? "yellowFill" : "blackFill");
                responseList.add(entry);
            });
            try {
                return jacksonWriter.getStringFromRequestObject(responseList);
            } catch (JsonProcessingException e) {
                return "[]";
            }
        }
        return "[]";

    }

    @RequestMapping(value = ApiPaths.WITNESS_REFRESH, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void refreshWitnesses() {

        final Document witnessEntry = Document.parse(getCoreMessage(new GetProducersCmd(ProducerListType.ALL)));
        if(!witnessEntry.isEmpty()){
            final MongoCollection<Document> collection = mongoClient.getDatabase("apex")
                    .getCollection("witnessStatus");
            final MongoCursor<Document> iter = collection.find().limit(1).iterator();
            if(iter.hasNext()) collection.findOneAndReplace(iter.next(), witnessEntry);
            else collection.insertOne(witnessEntry);
        }

    }

    private String getCoreMessage(final IRPCMessage msg){

        try {
            final String responseString = requestCaller.postRequest(rpcUrl, msg);
            final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class, responseString);
            return response.isSucceed() ? jacksonWriter.getStringFromRequestObject(response.getResult()) : "{}";
        } catch (Exception e) {
            log.error("RPC Endpoint connection error: " + rpcUrl);
            return "{}";
        }

    }

}
