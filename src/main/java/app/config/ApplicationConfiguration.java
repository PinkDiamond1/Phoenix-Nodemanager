package app.config;

import app.ManagerApplication;
import app.service.monitoring.telegram.TelegramSessionManager;
import com.mongodb.MongoClient;
import crypto.CryptoService;
import message.transaction.IProduceTransaction;
import message.transaction.TransactionFactory;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
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

    @Bean
    public RequestCallerService getCaller(){
        return new RequestCallerService();
    }

    @Bean
    public CryptoService getCryptoService(){ return new CryptoService();}

    @Bean
    public IProduceTransaction getTxFactory(){ return new TransactionFactory();}

    @Bean
    public TelegramSessionManager getTelegramSessionManager(){
        return new TelegramSessionManager();
    }

}
