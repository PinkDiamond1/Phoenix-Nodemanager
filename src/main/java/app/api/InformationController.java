package app.api;

import app.chart.IProvideLineChart;
import be.ceau.chart.LineChart;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import message.request.IRPCMessage;
import message.request.cmd.GetProducersCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping(ApiPaths.API)
public class InformationController {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RequestCallerService requestCaller;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    private IProvideLineChart lineChart;

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

    @GetMapping("/tpschart")
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

        final Optional<LineChart> chart = lineChart.getChart("Transactions",
                labelsList.toArray(new String[0]),
                pointsList.stream().mapToInt(i -> i).toArray());

        if(chart.isPresent()){
            return chart.get().isDrawable() ? chart.get().toJson() : "{}";
        }

        return "{}";

    }

    @RequestMapping(value = ApiPaths.WITNESS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getWitnesses() {

        return getCoreMessage(new GetProducersCmd("list"));

    }

    private String getCoreMessage(final IRPCMessage msg){

        try {
            final String responseString = requestCaller.postRequest(rpcUrl, msg);
            log.info(responseString);
            final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class, responseString);
            return response.isSucceed() ? jacksonWriter.getStringFromRequestObject(response.getResult()) : "{}";
        } catch (Exception e) {
            log.error("RPC Endpoint connection error: " + rpcUrl);
            return "{}";
        }

    }

}
