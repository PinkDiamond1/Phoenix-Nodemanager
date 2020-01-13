package api;

import app.api.InformationController;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import message.request.cmd.GetLatestBlockInfoCmd;
import message.request.cmd.GetProducersCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

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
    private MongoDatabase mongoDatabaseMock;


    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);
        when(mongoClientMock.getDatabase("apex")).thenReturn(mongoDatabaseMock);
        ReflectionTestUtils.setField(classUnderTest, "rpcUrl", "http://testurl");

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
