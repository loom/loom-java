package test.loom.eventsourcing;

import loom.eventsourcing.EventHandler;
import test.loom.User;
import test.loom.UserCreated;

public class UserCreatedEventHandler extends EventHandler<User, UserCreated> {

    @Override
    public User handleEvent(User user, UserCreated event) {
        return user
            .toBuilder()
            .username(event.getUsername())
            .passwordHash(event.getPasswordHash())
            .build();
    }
}
