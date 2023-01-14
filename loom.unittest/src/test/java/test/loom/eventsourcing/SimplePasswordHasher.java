package test.loom.eventsourcing;

public class SimplePasswordHasher implements PasswordHasher {

    @Override
    public String hashPassword(String providedPassword) {
        return providedPassword;
    }
}
