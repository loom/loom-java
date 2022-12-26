package loom.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class JsonData {
    private final String type;
    private final String content;
}
