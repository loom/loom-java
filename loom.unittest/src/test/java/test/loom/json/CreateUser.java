package test.loom.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateUser {
    private final String username;
    private final String password;
}
