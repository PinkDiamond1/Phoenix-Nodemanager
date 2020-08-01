package app.event.subscription;

import app.event.ManagerEvent;
import app.service.monitoring.IProvideMonitoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TelegramSubscription implements ISubscriber {

    private static final String headerAttention = "❗❗ ATTENTION ❗❗\n\n";
    private static final String headerInfo = "\uD83D\uDFE2 INFORMATION \uD83D\uDFE2+\n\n";

    @Autowired
    @Qualifier("TelegramBotRunner")
    private IProvideMonitoring monitoringProvider;

    @Override
    public void update(final ManagerEvent event) {
        Optional<String> message = Optional.empty();
        switch (event){
            case CORE_INSTALL:
                message = Optional.of(headerInfo + "APEX Blockchain Core was successfully installed");
                break;
            case CORE_INSTALL_FAILED:
                message = Optional.of(headerAttention + "APEX Blockchain Core failed to install");
                break;
            case CORE_START:
                message = Optional.of(headerInfo + "APEX Blockchain Core started");
                break;
            case CORE_START_FAILED:
                message = Optional.of(headerAttention + "APEX Blockchain Core failed to start");
                break;
            case CORE_STOP:
                message = Optional.of(headerInfo + "APEX Blockchain Core stopped");
                break;
            case CORE_STOP_FAILED:
                message = Optional.of(headerAttention + "APEX Blockchain Core failed to stop");
                break;
            case CORE_UPDATE:
                message = Optional.of(headerInfo + "APEX Blockchain Core was updated");
                break;
            case CORE_UPDATE_FAILED:
                message = Optional.of(headerAttention + "APEX Blockchain Core update failed");
                break;
            case LOGIN_FAIL:
                message = Optional.of(headerAttention + "APEX Nodemanager registered a failed Login attempt");
                break;
            case LOGIN_SUCCESS:
                message = Optional.of(headerInfo + "User logged into APEX Nodemanager");
                break;
        }
        message.ifPresent(msg -> monitoringProvider.executeMonitoringMessage(msg));
    }

}
