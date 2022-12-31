package test.loom.eventsourcing;

import loom.eventsourcing.EventHandler;
import test.loom.PasswordHashChanged;
import test.loom.User;

public class PasswordHashChangedEventHandler
    extends EventHandler<User, PasswordHashChanged> {

    public PasswordHashChangedEventHandler() {
        super(User.class, PasswordHashChanged.class);
    }

    @Override
    public User handleEvent(User state, PasswordHashChanged event) {
        return state.toBuilder().passwordHash(event.getPasswordHash()).build();
    }
}