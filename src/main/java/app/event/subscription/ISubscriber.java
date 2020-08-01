package app.event.subscription;

import app.event.ManagerEvent;

public interface ISubscriber {

    void update(ManagerEvent event);

}
