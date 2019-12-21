
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;

public class TestStuff {

    @Test
    public void test() {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("apex");
        System.out.println(database.getCollection("block").find().first().toJson());
    }

}
