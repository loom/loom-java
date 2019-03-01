package io.loom.eventsourcing;

import java.util.UUID;
import java.util.concurrent.Future;

public interface Snapshotter {
    Future<Void> takeSnapshot(UUID streamId);
}
