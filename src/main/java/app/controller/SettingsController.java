package app.controller;

import app.config.ApplicationPaths;
import app.entity.ApplicationUser;
import app.process.ProcessExecutor;
import app.repository.ApplicationUserRepository;
import app.settings.ConfigurationFileService;
import app.settings.component.ChainComponent;
import app.settings.component.RootComponent;
import app.settings.component.SettingsField;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
@RequestMapping("/" + ApplicationPaths.SETTINGS_PAGE)
public class SettingsController {

    private final Logger log = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    private ProcessExecutor processExecutor;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Value("${app.settings}")
    private String settingsPath;

    @Value("${app.settings.default}")
    private String settingsDefaultPath;

    @GetMapping
    public String getSettings(){
        return ApplicationPaths.SETTINGS_PAGE;
    }

    @PostMapping(params = "action=update")
    public String updateNodeManager() {

        new Thread(() -> processExecutor.updateManager()).start();
        return ApplicationPaths.INDEX_PATH;

    }

    @PostMapping(params = "action=change")
    public String changePassword(@RequestParam(value = "currentPassword") final String currentPassword,
                                 @RequestParam(value = "newPassword") final String newPassword,
                                 @RequestParam(value = "passwordRepeat") final String repeatPassword) {


        final Iterable<ApplicationUser> userIterable = userRepository.findAll();
        if(newPassword.equals(repeatPassword) && userIterable.iterator().hasNext()) {
            final ApplicationUser applicationUser = userIterable.iterator().next();
            if(applicationUser.getPassword().equals(currentPassword)){
                applicationUser.setPassword(newPassword);
                userRepository.save(applicationUser);
                return ApplicationPaths.LOGOUT_PATH;
            }
        }

        return ApplicationPaths.SETTINGS_PATH;

    }

    @PostMapping(params = "action=wipe")
    public String wipeData() {

        try {
            final RootComponent component = Files.isReadable(Paths.get(settingsPath)) ?
                    ConfigurationFileService.loadSettings(settingsPath) :
                    ConfigurationFileService.loadSettings(ResourceUtils.getFile(settingsDefaultPath).getAbsolutePath());
            final ArrayList<String> dirs = new ArrayList<>();
            final ChainComponent chainComponent = (ChainComponent) component.getComponents().get(ChainComponent.NAME);
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

        return ApplicationPaths.SETTINGS_PATH;

    }

}
