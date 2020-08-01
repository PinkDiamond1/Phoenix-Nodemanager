package app.service.monitoring.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

@Component
public class TelegramSessionManager {

    private BotSession session;
    private final TelegramBotsApi botsApi;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public TelegramSessionManager(){
        ApiContextInitializer.init();
        this.botsApi = new TelegramBotsApi();
    }

    public void addPollingBot(LongPollingBot bot){
        try {
            session = this.botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            log.error("Telegram Api bot registration failed" + e.getApiResponse());
        }
    }

    public void start() {
        if(session != null && !session.isRunning()){
            session.start();
        }
    }

    public void stop() {
        session.stop();
    }

}
