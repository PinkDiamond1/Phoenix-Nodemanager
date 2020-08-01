package app.service.monitoring.telegram;

import app.repository.TelegramConfigurationRepository;
import app.service.monitoring.IProvideMonitoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("TelegramBotRunner")
public class TelegramBotRunner implements IProvideMonitoring {

    @Autowired
    private TelegramSessionManager telegramSessionManager;

    @Autowired
    private TelegramConfigurationRepository configurationRepository;

    private TelegramMessageHandler messageHandler;

    @Override
    public void run() {
        configurationRepository.findFirstByTokenNotNull()
                .ifPresent(config -> {
                    if(config.getToken() != null && !config.getToken().equals("")) {
                        messageHandler = new TelegramMessageHandler(config.getToken(), config.getBotName(), config.getChatId(), configurationRepository);
                        telegramSessionManager.addPollingBot(messageHandler);
                        telegramSessionManager.start();
                    }
                });
    }

    @Override
    public void executeMonitoringMessage(final String msg) {
        if(messageHandler != null) messageHandler.executeMessage(msg);
    }
}
