package app.service.configuration;

import app.entity.ApplicationUser;
import app.process.ProcessExecutor;
import app.repository.ApplicationUserRepository;
import app.service.configuration.parse.IParseRootComponent;
import app.settings.ConfigurationFileService;
import app.settings.component.*;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import message.util.GenericJacksonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

@Primary
@Service("ConfigurationLogic")
public class ConfigurationLogic implements IAddRootComponentToModel, IGenericConfiguration {

    private final Logger log = LoggerFactory.getLogger(ConfigurationLogic.class);

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    @Qualifier("RootComponentParser")
    private IParseRootComponent parseRootComponent;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private ProcessExecutor processExecutor;

    @Autowired
    private MongoClient mongoClient;

    @Value("${app.settings}")
    private String settingsPath;

    @Value("${app.settings.default}")
    private String settingsDefaultPath;

    @Override
    public void loadToModel(final Model model) {
        try {
            final RootComponent component = Files.isReadable(Paths.get(settingsPath)) ?
                    loadSettings(settingsPath) :
                    loadSettings(ResourceUtils.getFile(settingsDefaultPath).getAbsolutePath());
            model.addAttribute(ActorComponent.NAME, jacksonWriter.getObjectFromString(ActorComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(ActorComponent.NAME))));
            model.addAttribute(ChainComponent.NAME, jacksonWriter.getObjectFromString(ChainComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(ChainComponent.NAME))));
            model.addAttribute(ConsensusComponent.NAME, jacksonWriter.getObjectFromString(ConsensusComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(ConsensusComponent.NAME))));
            model.addAttribute(MinerComponent.NAME, jacksonWriter.getObjectFromString(MinerComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(MinerComponent.NAME))));
            model.addAttribute(NetworkComponent.NAME, jacksonWriter.getObjectFromString(NetworkComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(NetworkComponent.NAME))));
            model.addAttribute(PluginsComponent.NAME, jacksonWriter.getObjectFromString(PluginsComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(PluginsComponent.NAME))));
            model.addAttribute(RpcComponent.NAME, jacksonWriter.getObjectFromString(RpcComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(RpcComponent.NAME))));
            model.addAttribute(RuntimeComponent.NAME, jacksonWriter.getObjectFromString(RuntimeComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(RuntimeComponent.NAME))));
        } catch (IOException e){
            log.error("Error while loading RootComponent to Model");
            Stream.of(e.getStackTrace()).forEach(s -> log.error(s.toString()));
        }
    }

    @Override
    public void save(final Map<String, Object> formParams) {
        try {
            writeSettings(settingsPath, parseRootComponent.getRootComponent(formParams));
        } catch (IOException e) {
            log.error("Error while writing RootComponent to path " + settingsPath);
            Stream.of(e.getStackTrace()).forEach(s -> log.error(s.toString()));
        }
    }

    void writeSettings(final String filename, final RootComponent settingsConf) throws IOException {
        final Config config1 = ConfigFactory.parseString(jacksonWriter.getStringFromRequestObject(settingsConf));
        try(FileOutputStream fos = new FileOutputStream(filename)) {
            try (DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos))) {
                outStream.writeBytes(config1.root().render(ConfigRenderOptions.concise()
                        .setFormatted(true)
                        .setJson(false)));
            }
        }
    }

    RootComponent loadSettings(final String filePath) throws IOException {
        final String configJson = ConfigFactory.parseFile(Paths.get(filePath).toFile())
                .root()
                .render(ConfigRenderOptions.concise()
                .setFormatted(true)
                .setJson(true));
        return jacksonWriter.getObjectFromString(RootComponent.class, configJson);
    }

    @Override
    public void updateApp() {
        new Thread(() -> processExecutor.updateManager()).start();
    }

    @Override
    public void wipeData() {
        try {
            final RootComponent component = Files.isReadable(Paths.get(settingsPath)) ?
                    ConfigurationFileService.loadSettings(settingsPath) :
                    ConfigurationFileService.loadSettings(ResourceUtils.getFile(settingsDefaultPath).getAbsolutePath());
            final ArrayList<String> dirs = new ArrayList<>();
            final ChainComponent chainComponent = jacksonWriter.getObjectFromString(ChainComponent.class,
                    jacksonWriter.getStringFromRequestObject(component.getComponents().get(ChainComponent.NAME)));
            dirs.add((String) chainComponent.getBlockBase().get(SettingsField.CHAIN_DIR));
            dirs.add((String) chainComponent.getForkBase().get(SettingsField.CHAIN_DIR));
            dirs.add((String) chainComponent.getDataBase().get(SettingsField.CHAIN_DIR));
            dirs.add("peers");
            dirs.add("logs");
            processExecutor.wipe(dirs);
            mongoClient.getDatabase("apex").drop();
        } catch (IOException e) {
            log.error("Wipe command failed");
        }
    }

    @Override
    public boolean changePassword(final String currentPassword, final String newPassword, final String repeatPassword) {
        final Iterable<ApplicationUser> userIterable = userRepository.findAll();
        if(newPassword.equals(repeatPassword) && userIterable.iterator().hasNext()) {
            final ApplicationUser applicationUser = userIterable.iterator().next();
            if(applicationUser.getPassword().equals(currentPassword)){
                applicationUser.setPassword(newPassword);
                userRepository.save(applicationUser);
                return true;
            }
        }
        return false;
    }

    @Override
    public void resetUser(final String username, final String password) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    if(user.getPassword().equals(password)){
                        userRepository.delete(user);
                    }
                });
    }
}
