package test.loom;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@Getter
public class User {
    private String username;
    private String passwordHash;

    public static User seedFactory() {
        return builder().build();
    }

    public User handleEvent(UserCreated event) {
        return toBuilder()
            .username(event.getUsername())
            .passwordHash(event.getPasswordHash())
            .build();
    }

    public User handleEvent(PasswordHashChanged event) {
        return toBuilder().passwordHash(event.getPasswordHash()).build();
    }
}
