package app.api;

import app.config.ApplicationPaths;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(ApplicationPaths.INFO_PATH)
public class InformationController {

    @Autowired
    private MongoClient mongoClient;

    @GetMapping("/height")
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
                .find().sort(new Document("executeTime", 1))
                .limit(1).iterator();
        return cursor.hasNext() ? (String) cursor.next().get("txHash") : "";
    }

}
