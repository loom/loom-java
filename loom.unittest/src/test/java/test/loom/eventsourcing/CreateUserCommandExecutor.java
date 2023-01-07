package test.loom.eventsourcing;

import static org.assertj.core.util.Arrays.asList;

import loom.eventsourcing.CommandExecutor;
import test.loom.CreateUser;
import test.loom.User;
import test.loom.UserCreated;

public class CreateUserCommandExecutor
    implements CommandExecutor<User, CreateUser> {

    private final PasswordHasher passwordHasher;

    public CreateUserCommandExecutor(PasswordHasher PasswordHasher) {
        passwordHasher = PasswordHasher;
    }

    @Override
    public Iterable<Object> produceEvents(User state, CreateUser command) {
        String providedPassword = command.getPassword();
        String passwordHash = passwordHasher.hashPassword(providedPassword);
        return asList(new UserCreated(command.getUsername(), passwordHash));
    }
}
