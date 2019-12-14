package app.controller;

import app.config.ApplicationPaths;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/error")
public class DefaultErrorController implements ErrorController {

    /**
     * Default error endpoint
     * @return an empty json to hide any backend related information
     */
    @GetMapping(value = "/error")
    public String returnError(){
        return ApplicationPaths.LOGIN_PATH;
    }

    /**
     * Error path override
     * @return The endpoint path string
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
