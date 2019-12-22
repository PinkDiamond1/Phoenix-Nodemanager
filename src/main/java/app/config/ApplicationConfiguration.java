package app.config;

import app.ManagerApplication;
import com.mongodb.MongoClient;
import message.util.GenericJacksonWriter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ManagerApplication.class);
    }

    @Bean
    public MongoClient mongo() {
        return new MongoClient("mongodb", 27017);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public GenericJacksonWriter getWriter(){
        return new GenericJacksonWriter();
    }

}
