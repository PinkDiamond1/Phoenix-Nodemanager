package app.controller;

import app.config.ApplicationPaths;
import app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @GetMapping(value = "/" + ApplicationPaths.LOGIN_PAGE)
    public String getLoginPage(){
        if(!applicationUserRepository.findAll().iterator().hasNext()) return ApplicationPaths.REGISTER_PATH;
        else return ApplicationPaths.LOGIN_PAGE;
    }

}
