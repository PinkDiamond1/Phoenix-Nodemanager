package app;

import app.config.PropertiesProfileLoader;
import app.event.EventConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ManagerApplication {

    @Autowired
    private EventConfiguration eventConfiguration;

    public static void main(String[] args) {
        PropertiesProfileLoader.initProperties();
        SpringApplication.run(ManagerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runTelegramBot() {
        eventConfiguration.configure();
    }
}
