package app;

import app.config.PropertiesProfileLoader;
import app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagerApplication {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    public static void main(String[] args){
        PropertiesProfileLoader.initProperties();
        SpringApplication.run(ManagerApplication.class, args);
    }

}
