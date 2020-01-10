package app.config;

import app.ManagerApplication;
import app.event.EventHandler;
import app.event.IHandleEvent;
import app.event.ManagerEvent;
import app.event.channel.CoreChannel;
import app.event.channel.ManagerChannel;
import app.event.subscription.AppSubscription;
import app.event.subscription.EmailSubscription;
import app.event.subscription.TelegramSubscription;
import com.mongodb.MongoClient;
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
    public EventHandler getEventHandler(){

        final TelegramSubscription telegramSubscription = new TelegramSubscription();
        final EmailSubscription emailSubscription = new EmailSubscription();
        final AppSubscription appSubscription = new AppSubscription();

        final CoreChannel coreChannel = new CoreChannel();
        coreChannel.addEvent(ManagerEvent.CORE_INSTALL);
        coreChannel.addEvent(ManagerEvent.CORE_STOP);
        coreChannel.addEvent(ManagerEvent.CORE_UPDATE);
        coreChannel.addEvent(ManagerEvent.CORE_START);
        coreChannel.subscribe(telegramSubscription);
        coreChannel.subscribe(emailSubscription);
        coreChannel.subscribe(appSubscription);

        final ManagerChannel managerChannel = new ManagerChannel();
        managerChannel.addEvent(ManagerEvent.LOGIN_SUCCESS);
        managerChannel.addEvent(ManagerEvent.LOGIN_FAIL);
        managerChannel.subscribe(telegramSubscription);
        managerChannel.subscribe(emailSubscription);

        final EventHandler eventHandler = new EventHandler();
        eventHandler.addChannel(coreChannel);
        eventHandler.addChannel(managerChannel);

        return eventHandler;

    }

}
