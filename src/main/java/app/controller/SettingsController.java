package app.controller;

import app.config.ApplicationPaths;
import app.process.ProcessExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/" + ApplicationPaths.SETTINGS_PAGE)
public class SettingsController {

    @Autowired
    private ProcessExecutor processExecutor;

    @GetMapping
    public String getSettings(){
        return ApplicationPaths.SETTINGS_PAGE;
    }

    @PostMapping(params = "action=update")
    public String runApexCore() {
        new Thread(() -> processExecutor.updateManager()).start();
        return ApplicationPaths.SETTINGS_PATH;
    }

}
