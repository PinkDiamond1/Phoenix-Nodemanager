package app.api;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import message.request.IRPCMessage;
import message.request.cmd.GetLatestBlockInfoCmd;
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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @GetMapping(ApiPaths.NODE_HEIGHT)
    @ResponseBody
    public long getCurrentBlockHeight() {
       final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("block")
                .find().sort(new Document("height", -1))
                .limit(1).iterator();
       return cursor.hasNext() ? (long) cursor.next().get("height") : 0L;
    }

    @GetMapping(ApiPaths.LAST_TX)
    @ResponseBody
    public String getLastTx() {
        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("transaction")
                .find().sort(new Document("createdAt", -1))
                .limit(1).iterator();
        return cursor.hasNext() ? dateFormat.format(cursor.next().get("createdAt")) : "";
    }

    @RequestMapping(value = ApiPaths.LAST_BLOCK, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLastBlock() {
        return getCoreMessage(new GetLatestBlockInfoCmd());
    }

    @RequestMapping(value = ApiPaths.WITNESS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getWitnesses() {
        return getCoreMessage(new GetProducersCmd());
    }

    private String getCoreMessage(final IRPCMessage msg){
        try {
            final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class,
                    requestCaller.postRequest(rpcUrl, msg));
            return response.isSucceed() ? jacksonWriter.getStringFromRequestObject(response.getResult()) : "{}";
        } catch (Exception e) {
            log.error("RPC Endpoint connection error: " + rpcUrl);
            return "{}";
        }
    }

}
