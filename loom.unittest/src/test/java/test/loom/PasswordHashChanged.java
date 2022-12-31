package test.loom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PasswordHashChanged {
    private final String passwordHash;
}
