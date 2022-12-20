package loom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class StreamCommand<T> {
    private final String streamId;
    private final T payload;
}
