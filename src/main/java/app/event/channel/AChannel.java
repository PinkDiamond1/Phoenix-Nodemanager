package app.event.channel;

import app.event.ManagerEvent;
import app.event.subscription.ISubscriber;

import java.util.ArrayList;
import java.util.List;

public abstract class AChannel {

    protected List<ISubscriber> subscribers = new ArrayList<>();
    protected List<ManagerEvent> managerEvents = new ArrayList<>();

    public void addEvent(final ManagerEvent event){
        managerEvents.add(event);
    }

    public void subscribe(final ISubscriber subscriber){
        subscribers.add(subscriber);
    }

    public void notifySubscribers(final ManagerEvent event){
        subscribers.forEach(subscriber -> {
            if(managerEvents.contains(event)) subscriber.update(event);
        });
    }

}
