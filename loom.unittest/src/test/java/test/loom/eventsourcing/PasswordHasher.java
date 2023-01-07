package test.loom.eventsourcing;

@FunctionalInterface
public interface PasswordHasher {

    String hashPassword(String providedPassword);
}
