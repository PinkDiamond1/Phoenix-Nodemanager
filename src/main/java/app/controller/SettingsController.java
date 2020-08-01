package app.controller;

import app.config.ApplicationPaths;
import app.service.configuration.ConfigurationLogic;
import app.service.configuration.IGenericConfiguration;
import app.service.configuration.IHandleTelegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/" + ApplicationPaths.SETTINGS_PAGE)
public class SettingsController {

    @Autowired
    @Qualifier("ConfigurationLogic")
    private IGenericConfiguration configurationLogic;

    @Autowired
    @Qualifier("ConfigurationLogic")
    private IHandleTelegram telegramLogic;

    @Autowired
    private ConfigurationLogic generalLogic;

    @GetMapping
    public String getSettings(Model model){
        generalLogic.loadTelegramData(model);
        return ApplicationPaths.SETTINGS_PAGE;
    }

    @PostMapping(params = "action=update")
    public String updateNodeManager() {
        configurationLogic.updateApp();
        return ApplicationPaths.INDEX_PATH;
    }

    @PostMapping(params = "action=change")
    public String changePassword(@RequestParam(value = "currentPassword") final String currentPassword,
                                 @RequestParam(value = "newPassword") final String newPassword,
                                 @RequestParam(value = "passwordRepeat") final String repeatPassword) {
        return configurationLogic.changePassword(currentPassword, newPassword, repeatPassword) ?
                ApplicationPaths.LOGOUT_PATH :
                ApplicationPaths.SETTINGS_PATH;
    }

    @PostMapping(params = "action=wipe")
    public String wipeData() {
        configurationLogic.wipeData();
        return ApplicationPaths.SETTINGS_PATH;
    }

    @PostMapping(params = "action=resetUser")
    public String resetUser(@RequestParam(value = "resetUsername") final String username,
                            @RequestParam(value = "resetPassword") final String password) {
        configurationLogic.resetUser(username, password);
        return ApplicationPaths.LOGOUT_PATH;
    }

    @PostMapping(params = "action=startTelegram")
    public String startTelegram(@RequestParam(value = "telegramBotToken") final String botToken,
                                @RequestParam(value = "telegramBotName") final String botName) {
        telegramLogic.startTelegram(botToken, botName);
        return ApplicationPaths.SETTINGS_PATH;
    }

}
