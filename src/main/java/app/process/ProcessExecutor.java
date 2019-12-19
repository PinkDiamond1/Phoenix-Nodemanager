package app.process;

import app.process.command.CommandFactory;
import app.repository.ProcessLoggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public class ProcessExecutor {

    private final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);

    @Autowired
    private ProcessLoggerRepository processLoggerRepository;

    public void installCore(final String branch, final String version) {

        final File workingDir = new File("APEX-Blockchain-Core");
        final String source = "build/libs/APEX-Blockchain-Core-"+version+".jar";
        final String target = "blockchain-core.jar";
        log.info("Installing core");
        try {
            new ProcessBuilder(CommandFactory.gitCheckout(branch).getCommand())
                    .directory(workingDir).start().waitFor();
            log.info("Checkout " + branch + " finished");
            new ProcessBuilder(CommandFactory.gradleShadowJar().getCommand())
                    .directory(workingDir).start().waitFor();
            log.info("Core build");
            new ProcessBuilder(CommandFactory.copy(source, target).getCommand())
                    .start().waitFor();
            log.info("Installation finished");
        } catch (InterruptedException | IOException e) {
            log.error("Installation failed: ");
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
        }

    }

}
