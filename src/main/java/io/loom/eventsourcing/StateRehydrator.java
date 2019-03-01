package io.loom.eventsourcing;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

public interface StateRehydrator<T> {
    Future<Optional<T>> tryRehydrateState(UUID streamId);
}
