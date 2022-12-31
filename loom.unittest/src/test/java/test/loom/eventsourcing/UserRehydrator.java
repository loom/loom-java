package test.loom.eventsourcing;

import java.util.Arrays;
import loom.eventsourcing.EventReader;
import loom.eventsourcing.Rehydrator;
import test.loom.User;

public class UserRehydrator extends Rehydrator<User> {

    public UserRehydrator(EventReader eventReader) {
        super(
            () -> User.builder().build(),
            eventReader,
            Arrays.asList(
                new UserCreatedEventHandler(),
                new PasswordHashChangedEventHandler()
            ));
    }
}
