package loom.eventsourcing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Snapshot<S> {
    private final String streamId;
    private final long version;
    private final S state;

    static <S> Snapshot<S> seed(String streamId, S seed) {
        return new Snapshot<>(streamId, 0, seed);
    }

    Snapshot<S> next(S newState) {
        return new Snapshot<>(streamId, version + 1, newState);
    }
}
