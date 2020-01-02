package app.api;

import app.config.ApplicationPaths;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
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

import java.text.SimpleDateFormat;

@Controller
@RequestMapping(ApplicationPaths.INFO_PATH)
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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @GetMapping("/nodeHeight")
    @ResponseBody
    public long getCurrentBlockHeight() {
       final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("block")
                .find().sort(new Document("height", -1))
                .limit(1).iterator();
       return cursor.hasNext() ? (long) cursor.next().get("height") : 0L;
    }

    @GetMapping("/lasttx")
    @ResponseBody
    public String getLastTx() {
        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("transaction")
                .find().sort(new Document("createdAt", -1))
                .limit(1).iterator();
        return cursor.hasNext() ? dateFormat.format(cursor.next().get("createdAt")) : "";
    }

    @RequestMapping(value = "/witness", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getWitnesses() {
        try {
            final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class,
                    requestCaller.postRequest(rpcUrl, new GetProducersCmd()));
            return response.isSucceed() ? jacksonWriter.getStringFromRequestObject(response.getResult()) : "{}";
        } catch (Exception e) {
            log.error("RPC Endpoint connection error: " + rpcUrl);
            return "{}";
        }
    }

}
