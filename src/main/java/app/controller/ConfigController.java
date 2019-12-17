package app.controller;

import app.config.ApplicationPaths;
import app.settings.ConfigurationFileService;
import app.settings.component.*;
import message.util.GenericJacksonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Controller
@RequestMapping("/" + ApplicationPaths.CONFIG_PAGE)
public class ConfigController {

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${app.settings}")
    private String settingsPath;

    @Value("${app.settings.default}")
    private String settingsDefaultPath;

    @GetMapping
    public String getConfig(Model model) throws IOException {
        final RootComponent component = Files.isReadable(Paths.get(settingsPath)) ?
                ConfigurationFileService.loadSettings(settingsPath) :
                ConfigurationFileService.loadSettings(ResourceUtils.getFile(settingsDefaultPath).getAbsolutePath());
        model.addAttribute(ActorComponent.NAME, jacksonWriter.getObjectFromString(ActorComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(ActorComponent.NAME))));
        model.addAttribute(ChainComponent.NAME, jacksonWriter.getObjectFromString(ChainComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(ChainComponent.NAME))));
        model.addAttribute(ConsensusComponent.NAME, jacksonWriter.getObjectFromString(ConsensusComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(ConsensusComponent.NAME))));
        model.addAttribute(MinerComponent.NAME, jacksonWriter.getObjectFromString(MinerComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(MinerComponent.NAME))));
        model.addAttribute(NetworkComponent.NAME, jacksonWriter.getObjectFromString(NetworkComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(NetworkComponent.NAME))));
        model.addAttribute(PluginsComponent.NAME, jacksonWriter.getObjectFromString(PluginsComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(PluginsComponent.NAME))));
        model.addAttribute(RpcComponent.NAME, jacksonWriter.getObjectFromString(RpcComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(RpcComponent.NAME))));
        model.addAttribute(RuntimeComponent.NAME, jacksonWriter.getObjectFromString(RuntimeComponent.class,
                jacksonWriter.getStringFromRequestObject(component.getComponents().get(RuntimeComponent.NAME))));
        return ApplicationPaths.CONFIG_PAGE;
    }

    @PostMapping
    public String postConfig(@RequestParam Map<String, Object> formParams){
        System.out.println(formParams.toString());
        return ApplicationPaths.CONFIG_PAGE;
    }

}
