package loom.eventsourcing;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class StreamEvent<T> {
    private final String streamId;
    private final long version;
    private final LocalDateTime raisedTimeUtc;
    private final T payload;
}
