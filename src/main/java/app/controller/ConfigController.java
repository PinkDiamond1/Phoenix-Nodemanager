package app.controller;

import app.config.ApplicationPaths;
import app.service.configuration.IAddRootComponentToModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/" + ApplicationPaths.CONFIG_PAGE)
public class ConfigController {

    @Autowired
    @Qualifier("ConfigurationLogic")
    private IAddRootComponentToModel configurationLogic;

    @GetMapping
    public String getConfig(final Model model) {
        configurationLogic.loadToModel(model);
        return ApplicationPaths.CONFIG_PAGE;
    }

    @PostMapping
    public String postConfig(@RequestParam final Map<String, Object> formParams) {
        configurationLogic.save(formParams);
        return ApplicationPaths.CONFIG_PATH;
    }

}
