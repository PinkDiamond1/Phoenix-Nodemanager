package app.controller;

import app.config.ApplicationPaths;
import app.entity.ApplicationUser;
import app.repository.ApplicationUserRepository;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/" +  ApplicationPaths.REGISTER_PAGE)
public class RegisterController {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @GetMapping
    public String getRegisterPage(Model model){
        if(applicationUserRepository.findAll().iterator().hasNext()) return ApplicationPaths.LOGIN_PATH;
        else {
            model.addAttribute("secret", Base32.random());
            return ApplicationPaths.REGISTER_PAGE;
        }
    }

    @PostMapping
    public String postRegisterPage(@RequestParam(value = "inputUsername") final String username,
                                   @RequestParam(value = "inputPassword") final String password,
                                   @RequestParam(value = "secret") final String secret) {
        if(!applicationUserRepository.findAll().iterator().hasNext()) {
            final ApplicationUser initialUser = ApplicationUser.builder()
                    .username(username)
                    .password(password)
                    .secret(secret)
                    .enabled(true)
                    .build();
            applicationUserRepository.save(initialUser);
        }
        return ApplicationPaths.LOGIN_PATH;
    }

}
