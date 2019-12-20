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
    public static final String JAR_NAME = "blockchain-core.jar";

    @Autowired
    private ProcessLoggerRepository processLoggerRepository;

    public void installCore(final String branch, final String version) {

        final File workingDir = new File("APEX-Blockchain-Core");
        final String source = "APEX-Blockchain-Core/build/libs/APEX-Blockchain-Core-"+version+".jar";
        log.info("Installing core");
        logProcess(ProcessStatus.INSTALL);
        try {
            log.info("Start Checkout");
            new ProcessBuilder(CommandFactory.gitCheckout(branch).getCommand())
                    .directory(workingDir)
                    .inheritIO()
                    .start()
                    .waitFor();
            log.info("Git pull");
            new ProcessBuilder(CommandFactory.gitPull().getCommand())
                    .directory(workingDir)
                    .inheritIO()
                    .start()
                    .waitFor();
            log.info("Git pull finished");
            log.info("Start core build");
            new ProcessBuilder(CommandFactory.gradleShadowJar().getCommand())
                    .inheritIO()
                    .directory(workingDir)
                    .start()
                    .waitFor();
            log.info("Core build finished");
            log.info("Copy new jar");
            new ProcessBuilder(CommandFactory.copy(source, JAR_NAME).getCommand())
                    .inheritIO()
                    .start()
                    .waitFor();
            logProcess(ProcessStatus.FINISHED);
            log.info("Installation finished");
        } catch (InterruptedException | IOException e) {
            logProcess(ProcessStatus.FAILED);
            log.error("Installation failed: ");
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
        }

    }

    public void runJar() {

        try {
            log.info("Started jar");
            final Process process = new ProcessBuilder(CommandFactory.runJar(JAR_NAME).getCommand())
                    .inheritIO()
                    .start();
            logProcess(ProcessStatus.RUNNING, process.pid());
        } catch (IOException e){
            logProcess(ProcessStatus.FAILED);
            log.error("Running jar failed");
            Stream.of(e.getStackTrace()).forEach(stackTraceElement -> log.error(stackTraceElement.toString()));
        }

    }

    public void stopJar(){

        processLoggerRepository.findById(JAR_NAME).ifPresent(processLogger -> {
            if(processLogger.getStatus().equals(ProcessStatus.RUNNING.getStatus())) {
                ProcessHandle.of(processLogger.getPid()).ifPresent(ProcessHandle::destroy);
                logProcess(ProcessStatus.FINISHED);
                log.info("Stopped jar");
            }
        });

    }

    private void logProcess(final ProcessStatus status){

        processLoggerRepository.save(ProcessLogger.builder()
                .name(JAR_NAME)
                .pid(0)
                .status(status.getStatus())
                .timestamp(Instant.now().toEpochMilli())
                .build());

    }

    private void logProcess(final ProcessStatus status, final long pid){

        processLoggerRepository.save(ProcessLogger.builder()
                .name(JAR_NAME)
                .pid(pid)
                .status(status.getStatus())
                .timestamp(Instant.now().toEpochMilli())
                .build());

    }

}
