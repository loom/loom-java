package test.loom.eventsourcing;

import loom.eventsourcing.EventHandler;
import test.loom.User;
import test.loom.UserCreated;

public class UserCreatedEventHandler
    implements EventHandler<User, UserCreated> {

    @Override
    public User handleEvent(User state, UserCreated event) {
        return state.handleEvent(event);
    }
}
