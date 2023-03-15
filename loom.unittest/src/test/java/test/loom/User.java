package test.loom;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@Getter
public class User {
    private String id;
    private String username;
    private String passwordHash;

    public static User seedFactory(String id) {
        return builder().id(id).build();
    }

    public User handleEvent(UserCreated event) {
        return toBuilder().username(event.getUsername()).build();
    }

    public User handleEvent(PasswordHashChanged event) {
        return toBuilder().passwordHash(event.getPasswordHash()).build();
    }
}
