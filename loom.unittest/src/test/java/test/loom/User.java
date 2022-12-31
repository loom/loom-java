package test.loom;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class User {
    private String username;
    private String passwordHash;
}
