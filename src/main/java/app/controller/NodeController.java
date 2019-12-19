package app.controller;

import app.config.ApplicationPaths;
import app.entity.ProcessLogger;
import app.repository.ProcessLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.LOGIN_PAGE)
public class NodeController {

    @Autowired
    private ProcessLoggerRepository processLoggerRepository;

    @GetMapping
    public String getNode(){
        return ApplicationPaths.NODE_PAGE;
    }

    @PostMapping
    public String installApexCore() throws IOException, InterruptedException {

        new ProcessBuilder("git", "checkout", "master")
                .directory(new File("~/APEX-Blockchain-Core"))
                .start().waitFor();

        new ProcessBuilder("gradle", "shadowJar")
                .directory(new File("~/APEX-Blockchain-Core"))
                .start().waitFor();

        new ProcessBuilder("cp", "build/libs/APEX-Blockchain-Core-0.9.2.jar", "~/apex-blockchain-core.jar")
                .directory(new File("~/APEX-Blockchain-Core"))
                .start().waitFor();

        return ApplicationPaths.NODE_PATH;
    }

}
