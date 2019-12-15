package app;

import app.config.PropertiesProfileLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagerApplication {

    public static void main(String[] args) {
        PropertiesProfileLoader.initProperties();
        SpringApplication.run(ManagerApplication.class, args);
    }

}
