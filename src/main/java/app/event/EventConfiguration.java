package app.event;

import app.event.channel.CoreChannel;
import app.event.channel.ManagerChannel;
import app.event.subscription.TelegramSubscription;
import app.service.monitoring.IProvideMonitoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventConfiguration {

    @Autowired
    private IProvideMonitoring monitoringProvider;

    @Autowired
    private TelegramSubscription telegramSubscription;

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private CoreChannel coreChannel;

    @Autowired
    private ManagerChannel managerChannel;

    public void configure(){
        // Core Channel Conf
        coreChannel.addEvent(ManagerEvent.CORE_INSTALL);
        coreChannel.addEvent(ManagerEvent.CORE_STOP);
        coreChannel.addEvent(ManagerEvent.CORE_UPDATE);
        coreChannel.addEvent(ManagerEvent.CORE_START);
        coreChannel.subscribe(telegramSubscription);
        // Manager Channel Conf
        managerChannel.addEvent(ManagerEvent.LOGIN_SUCCESS);
        managerChannel.addEvent(ManagerEvent.LOGIN_FAIL);
        managerChannel.subscribe(telegramSubscription);
        // Add Channels
        eventHandler.addChannel(coreChannel);
        eventHandler.addChannel(managerChannel);
        // Run Monitoring Provider
        monitoringProvider.run();
    }
}
