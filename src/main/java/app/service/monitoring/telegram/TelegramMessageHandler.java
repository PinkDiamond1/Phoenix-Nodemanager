package app.service.monitoring.telegram;

import app.repository.TelegramConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramMessageHandler extends ATelegramBot {

    private final TelegramConfigurationRepository repository;

    private final Logger log = LoggerFactory.getLogger(TelegramMessageHandler.class);

    private Long chatId;

    private static final String welcome = "\uD83D\uDE0E Welcome! I'm now up and running";

    public TelegramMessageHandler(final String token, final String botname, final Long chatId,
                                  final TelegramConfigurationRepository repository) {
        super(token, botname);
        this.repository = repository;
        this.chatId = chatId;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            final Long chatIdUpdate = update.getMessage().getChatId();
            if (chatId != null) {
                if (chatIdUpdate.equals(chatId)) {
                    final SendMessage msg = new SendMessage();
                    msg.setChatId(chatId);
                    msg.setText(welcome);
                    execute(msg);
                }
            } else {
                repository.findFirstByTokenNotNull()
                        .ifPresent(config -> {
                            config.setChatId(update.getMessage().getChatId());
                            repository.save(config);
                            this.chatId = update.getMessage().getChatId();
                        });
            }
        } catch (TelegramApiException e){
            log.info("Telegram API Exception. Ignore");
        }
    }

    public void executeMessage(final String text) {
        try {
            if (chatId != null) {
                final SendMessage msg = new SendMessage();
                msg.setChatId(chatId);
                msg.setText(text);
                execute(msg);
            }
        } catch (TelegramApiException e){
            log.info("Telegram API Exception. Ignore");
        }
    }
}