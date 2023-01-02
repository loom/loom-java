package loom.eventsourcing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Snapshot<S> {
    private final String streamId;
    private final long version;
    private final S state;

    Snapshot<S> next(S newState) {
        return new Snapshot<>(streamId, version + 1, newState);
    }
}
