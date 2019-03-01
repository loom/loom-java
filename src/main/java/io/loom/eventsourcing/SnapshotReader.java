package io.loom.eventsourcing;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

public interface SnapshotReader<T> {
    Future<Optional<T>> tryRestoreSnapshot(UUID streamId);
}
