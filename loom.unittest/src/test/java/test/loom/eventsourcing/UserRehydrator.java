package test.loom.eventsourcing;

import java.util.Arrays;
import loom.eventsourcing.EventReader;
import loom.eventsourcing.Rehydrator;
import test.loom.User;

public class UserRehydrator extends Rehydrator<User> {

    public UserRehydrator(EventReader eventReader) {
        super(
            User.class,
            eventReader,
            () -> User.builder().build(),
            Arrays.asList(
                new UserCreatedEventHandler(),
                new PasswordHashChangedEventHandler()
            ));
    }
}
