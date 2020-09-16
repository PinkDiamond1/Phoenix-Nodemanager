package app.service.configuration;

import app.process.ProcessExecutor;
import app.repository.ApplicationUserRepository;
import app.repository.TelegramConfigurationRepository;
import app.service.configuration.parse.IParseRootComponent;
import app.service.configuration.parse.RootComponentParser;
import app.service.monitoring.IProvideMonitoring;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import message.util.GenericJacksonWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationLogicTest {

    @Spy
    private GenericJacksonWriter jacksonWriter = new GenericJacksonWriter();

    @Spy
    private IParseRootComponent parseRootComponent = new RootComponentParser();

    @Mock
    private ApplicationUserRepository userRepository;

    @Mock
    private TelegramConfigurationRepository telegramRepository;

    @Mock
    private ProcessExecutor processExecutor;

    @Mock
    private IProvideMonitoring telegramBotRunner;

    @Mock
    private MongoClient mongoClient;

    @InjectMocks
    private ConfigurationLogic classUnderTest;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(classUnderTest, "settingsPath", "settings.conf");
        ReflectionTestUtils.setField(classUnderTest, "settingsDefaultPath", "settings.conf");
    }

    @Test
    void when_loadToModelOk_then_RootComponentWasLoadedAndNoException(){
        // arrange
        Model model = mock(Model.class);

        //act
        classUnderTest.loadToModel(model);
    }

    @Test
    void when_loadToModelHasNoSettingsPath_then_RootComponentWasLoadedWithDefaultPathAndNoException(){
        // arrange
        ReflectionTestUtils.setField(classUnderTest, "settingsPath", "/wrong/path/settings.conf");
        Model model = mock(Model.class);

        //act
        classUnderTest.loadToModel(model);
    }

    @Test
    void when_loadToModelInvalidData_then_IOExceptionGetsCaught() throws JsonProcessingException {
        // arrange
        Model model = mock(Model.class);
        when(jacksonWriter.getStringFromRequestObject(any())).thenThrow(JsonProcessingException.class);

        //act
        classUnderTest.loadToModel(model);
    }

}