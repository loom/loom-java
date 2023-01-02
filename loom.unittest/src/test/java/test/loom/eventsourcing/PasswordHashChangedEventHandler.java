package test.loom.eventsourcing;

import loom.eventsourcing.EventHandler;
import test.loom.PasswordHashChanged;
import test.loom.User;

public class PasswordHashChangedEventHandler
    extends EventHandler<User, PasswordHashChanged> {

    @Override
    public User handleEvent(User state, PasswordHashChanged event) {
        return state.toBuilder().passwordHash(event.getPasswordHash()).build();
    }
}