package test.loom.eventsourcing;

import java.util.Arrays;
import loom.eventsourcing.CommandExecutor;
import test.loom.CreateUser;
import test.loom.PasswordHashChanged;
import test.loom.User;
import test.loom.UserCreated;

public class CreateUserCommandExecutor
    implements CommandExecutor<User, CreateUser> {

    private final PasswordHasher passwordHasher;

    public CreateUserCommandExecutor(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Iterable<Object> produceEvents(User state, CreateUser command) {
        String providedPassword = command.getPassword();
        String passwordHash = passwordHasher.hashPassword(providedPassword);
        return Arrays.asList(
            new UserCreated(command.getUsername()),
            new PasswordHashChanged(passwordHash));
    }
}
