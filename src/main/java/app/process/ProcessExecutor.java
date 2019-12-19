package app.process;

import app.entity.ProcessLogger;
import app.process.command.CommandFactory;
import app.repository.ProcessLoggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.stream.Stream;

@Service
public class ProcessExecutor {

    private final Logger log = LoggerFactory.getLogger(ProcessExecutor.class);

    @Autowired
    private ProcessLoggerRepository processLoggerRepository;

    public void installCore(final String branch, final String version) {

        final File workingDir = new File("APEX-Blockchain-Core");
        final String source = "APEX-Blockchain-Core/build/libs/APEX-Blockchain-Core-"+version+".jar";
        final String target = "blockchain-core.jar";
        log.info("Installing core");
        try {
            log.info("Start Checkout");
            new ProcessBuilder(CommandFactory.gitCheckout(branch).getCommand())
                    .directory(workingDir)
                    .inheritIO()
                    .start()
                    .waitFor();
            log.info("Checkout " + branch + " finished");
            log.info("Start core build");
            new ProcessBuilder(CommandFactory.gradleShadowJar().getCommand())
                    .inheritIO()
                    .directory(workingDir)
                    .start()
                    .waitFor();
            log.info("Core build finished");
            new ProcessBuilder(CommandFactory.copy(source, target).getCommand())
                    .inheritIO()
                    .start()
                    .waitFor();
            log.info("Installation finished");
        } catch (InterruptedException | IOException e) {
            log.error("Installation failed: ");
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
        }

    }

    public void runJar(final String jarPath) {
        try {
            log.info("Started Jar " + jarPath);
            final Process process = new ProcessBuilder(CommandFactory.runJar(jarPath).getCommand())
                    .inheritIO()
                    .start();
            processLoggerRepository.save(ProcessLogger.builder()
                    .name("blockchain-core")
                    .pid(process.pid())
                    .status(ProcessStatus.RUNNING.getStatus())
                    .timestamp(Instant.now().toEpochMilli())
                    .build());
        } catch (IOException e){
            processLoggerRepository.save(ProcessLogger.builder()
                    .name("blockchain-core")
                    .pid(0)
                    .status(ProcessStatus.FAILED.getStatus())
                    .timestamp(Instant.now().toEpochMilli())
                    .build());
            log.error("Running jar " + jarPath + "failed");
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
        }

    }

}
