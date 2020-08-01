package app.event;

import app.event.channel.AChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventHandler implements IHandleEvent {

    private final List<AChannel> channels = new ArrayList<>();

    public void addChannel(final AChannel channel){
        channels.add(channel);
    }

    @Override
    public void handleEvent(final ManagerEvent event) {
        channels.forEach(channel -> channel.notifySubscribers(event));
    }

}
