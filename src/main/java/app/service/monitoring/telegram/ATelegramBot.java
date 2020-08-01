package app.service.monitoring.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class ATelegramBot extends TelegramLongPollingBot {

    private final String token;
    private final String botname;

    public ATelegramBot(final String token, final String botname){
        this.token = token;
        this.botname = botname;
    }

    @Override
    public abstract void onUpdateReceived(Update update);

    @Override
    public String getBotUsername() {
        return botname;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}
