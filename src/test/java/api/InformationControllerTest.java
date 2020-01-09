package api;

import app.api.InformationController;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import message.request.cmd.GetLatestBlockInfoCmd;
import message.request.cmd.GetProducersCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class InformationControllerTest {

    @InjectMocks
    private InformationController classUnderTest;

    @Mock
    private MongoClient mongoClientMock;

    @Mock
    private RequestCallerService requestCallerServiceMock;

    @Mock
    private GenericJacksonWriter genericJacksonWriterMock;

    @Mock
    private MongoCursor<Document> mongoCursorMock;

    @Mock
    private MongoDatabase mongoDatabaseMock;

    @Mock
    private MongoCollection<Document> mongoCollectionMock;

    @Mock
    private FindIterable<Document> findIterableMock;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);
        when(mongoClientMock.getDatabase("apex")).thenReturn(mongoDatabaseMock);
        ReflectionTestUtils.setField(classUnderTest, "rpcUrl", "http://testurl");

    }

    @Test
    public void testGetCurrentBlockHeight() {

        // Setup
        final long expectedResult = 1L;

        // Configure
        when(mongoDatabaseMock.getCollection("block")).thenReturn(mongoCollectionMock);
        when(mongoCollectionMock.find()).thenReturn(findIterableMock);
        when(findIterableMock.limit(1)).thenReturn(findIterableMock);
        when(findIterableMock.sort(new Document("height", -1))).thenReturn(findIterableMock);
        when(findIterableMock.iterator()).thenReturn(mongoCursorMock);
        when(mongoCursorMock.next()).thenReturn(new Document("height", 1L));

        // Run 1
        when(mongoCursorMock.hasNext()).thenReturn(true);
        final long result1 = classUnderTest.getCurrentBlockHeight();

        // Run 2
        when(mongoCursorMock.hasNext()).thenReturn(false);
        final long result2 = classUnderTest.getCurrentBlockHeight();

        // Verify
        assertEquals(expectedResult, result1);
        assertEquals(0L , result2);

    }

    @Test
    public void testGetLastTx() {

        // Setup
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        final Date testDate = new Date();
        final String expectedResult = dateFormat.format(testDate);

        // Configure
        when(mongoDatabaseMock.getCollection("transaction")).thenReturn(mongoCollectionMock);
        when(mongoCollectionMock.find()).thenReturn(findIterableMock);
        when(findIterableMock.limit(1)).thenReturn(findIterableMock);
        when(findIterableMock.sort(new Document("createdAt", -1))).thenReturn(findIterableMock);
        when(findIterableMock.iterator()).thenReturn(mongoCursorMock);
        when(mongoCursorMock.next()).thenReturn(new Document("createdAt", testDate));

        // Run 1
        when(mongoCursorMock.hasNext()).thenReturn(true);
        final String result1 = classUnderTest.getLastTx();

        // Run 2
        when(mongoCursorMock.hasNext()).thenReturn(false);
        final String result2 = classUnderTest.getLastTx();

        // Verify
        assertEquals(expectedResult, result1);
        assertEquals("", result2);

    }

    @Test
    public void testGetLastBlock() throws Exception {

        // Setup
        final String expectedResult = "resultObjectLastBlock";
        final ExecResult execResult = ExecResult.builder()
                .status(200)
                .succeed(true)
                .message("msg")
                .result(new HashMap<>())
                .build();
        when(requestCallerServiceMock.postRequest(anyString(), any(GetLatestBlockInfoCmd.class)))
                .thenReturn("jsonStringLastBlock");
        when(genericJacksonWriterMock.getObjectFromString(ExecResult.class, "jsonStringLastBlock"))
                .thenReturn(execResult);
        when(genericJacksonWriterMock.getStringFromRequestObject(any())).thenReturn("resultObjectLastBlock");

        // Run 1
        final String result1 = classUnderTest.getLastBlock();

        // Run 2
        execResult.setStatus(500);
        execResult.setSucceed(false);
        when(genericJacksonWriterMock.getObjectFromString(ExecResult.class, "jsonStringLastBlock"))
                .thenReturn(execResult);
        final String result2 = classUnderTest.getLastBlock();

        // Verify
        assertEquals(expectedResult, result1);
        assertEquals("{}", result2);

    }

    @Test
    public void testGetWitnesses() throws Exception {

        // Setup
        final String expectedResult = "resultObjectWitness";
        final ExecResult execResult = ExecResult.builder()
                .status(200)
                .succeed(true)
                .message("msg")
                .result(new HashMap<>())
                .build();
        when(requestCallerServiceMock.postRequest(anyString(), any(GetProducersCmd.class)))
                .thenReturn("jsonStringWitness");
        when(genericJacksonWriterMock.getObjectFromString(ExecResult.class, "jsonStringWitness"))
                .thenReturn(execResult);
        when(genericJacksonWriterMock.getStringFromRequestObject(any())).thenReturn("resultObjectWitness");

        // Run 1
        final String result1 = classUnderTest.getWitnesses();

        // Run 2
        execResult.setStatus(500);
        execResult.setSucceed(false);
        when(genericJacksonWriterMock.getObjectFromString(ExecResult.class, "jsonStringWitness"))
                .thenReturn(execResult);
        final String result2 = classUnderTest.getWitnesses();

        // Verify
        assertEquals(expectedResult, result1);
        assertEquals("{}", result2);

    }

    @Test
    public void testGetWitnessesOnException() {

        // Setup
        final String expectedResult = "{}";

        // Run
        final String result = classUnderTest.getWitnesses();

        // Verify
        assertEquals(expectedResult, result);

    }

}
