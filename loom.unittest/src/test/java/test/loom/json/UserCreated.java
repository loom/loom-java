package test.loom.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCreated {
    private final String username;
    private final String passwordHash;
}
