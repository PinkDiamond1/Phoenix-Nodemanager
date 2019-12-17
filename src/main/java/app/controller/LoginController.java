package app.controller;

import app.config.ApplicationPaths;
import app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.LOGIN_PAGE)
public class LoginController {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @GetMapping
    public String getLoginPage(){
        if(!applicationUserRepository.findAll().iterator().hasNext()) return ApplicationPaths.REGISTER_PATH;
        else return ApplicationPaths.LOGIN_PAGE;
    }

}
