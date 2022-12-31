package loom.eventsourcing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Snapshot<T> {
    private final String streamId;
    private final long version;
    private final T state;
}
