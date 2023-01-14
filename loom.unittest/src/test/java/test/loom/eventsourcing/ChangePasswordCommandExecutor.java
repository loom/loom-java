package test.loom.eventsourcing;

import java.util.Arrays;
import loom.eventsourcing.CommandExecutor;
import test.loom.ChangePassword;
import test.loom.PasswordHashChanged;
import test.loom.User;

public class ChangePasswordCommandExecutor
    implements CommandExecutor<User, ChangePassword> {

    private final PasswordHasher passwordHasher;

    public ChangePasswordCommandExecutor(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Iterable<Object> produceEvents(User state, ChangePassword command) {
        String providedPassword = command.getPassword();
        String passwordHash = passwordHasher.hashPassword(providedPassword);
        return Arrays.asList(new PasswordHashChanged(passwordHash));
    }
}
