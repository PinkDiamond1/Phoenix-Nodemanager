package app.controller;

import app.config.ApplicationPaths;
import app.process.ProcessExecutor;
import app.repository.ProcessLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.NODE_PAGE)
public class NodeController {

    @Autowired
    private ProcessExecutor processExecutor;

    @Autowired
    private ProcessLoggerRepository processLoggerRepository;

    @GetMapping
    public String getNode(Model model) {
        processLoggerRepository.findById(ProcessExecutor.JAR_NAME).ifPresentOrElse(
                process -> model.addAttribute("nodeStatus", process.getStatus()),
                () -> model.addAttribute("nodeStatus", "no"));
        return ApplicationPaths.NODE_PAGE;
    }

    @PostMapping(params = "action=install")
    public String installApexCore(@RequestParam(value = "release", required = false) final String release,
                                  @RequestParam(value = "commit", required = false) final String commit) {
        if(release != null && commit != null)
            new Thread(() -> processExecutor.installCore(commit, release)).start();
        else
            new Thread(() -> processExecutor.installCore("master", "0.9.2")).start();
        return ApplicationPaths.NODE_PATH;
    }

    @PostMapping(params = "action=run")
    public String runApexCore() throws InterruptedException {
        new Thread(() -> processExecutor.runJar()).start();
        Thread.sleep(1000L);
        return ApplicationPaths.NODE_PATH;
    }

    @PostMapping(params = "action=stop")
    public String stopApexCore() throws InterruptedException {
        processExecutor.stopJar();
        Thread.sleep(5000L);
        return ApplicationPaths.NODE_PATH;
    }

}
