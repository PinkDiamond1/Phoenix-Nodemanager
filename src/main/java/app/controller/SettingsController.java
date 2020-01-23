package app.controller;

import app.config.ApplicationPaths;
import app.entity.ApplicationUser;
import app.process.ProcessExecutor;
import app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/" + ApplicationPaths.SETTINGS_PAGE)
public class SettingsController {

    @Autowired
    private ProcessExecutor processExecutor;


    @Autowired
    private ApplicationUserRepository userRepository;

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

}
