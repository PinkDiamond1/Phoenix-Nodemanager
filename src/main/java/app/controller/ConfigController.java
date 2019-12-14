package app.controller;

import app.config.ApplicationPaths;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/" + ApplicationPaths.CONFIG_PAGE)
public class ConfigController {

    @GetMapping
    public String getConfig(){
        return ApplicationPaths.CONFIG_PAGE;
    }

}
