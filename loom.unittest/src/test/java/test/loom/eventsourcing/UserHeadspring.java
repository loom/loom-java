package test.loom.eventsourcing;

import java.util.Arrays;
import loom.eventsourcing.EventStore;
import loom.eventsourcing.Headspring;
import test.loom.User;

public class UserHeadspring extends Headspring<User> {

    private static final PasswordHasher passwordHasher = new SimplePasswordHasher();

    public UserHeadspring(EventStore eventStore) {
        super(
            eventStore,
            User::seedFactory,
            Arrays.asList(
                new CreateUserCommandExecutor(passwordHasher),
                new ChangePasswordCommandExecutor(passwordHasher),
                new RaiseUnknownEventCommandExecutor()
            ),
            Arrays.asList(
                new UserCreatedEventHandler(),
                new PasswordHashChangedEventHandler()
            ));
    }
}
