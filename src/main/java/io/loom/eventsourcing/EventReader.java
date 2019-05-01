package io.loom.eventsourcing;

import java.util.UUID;
import java.util.concurrent.Future;

public interface EventReader {
    Future<Iterable<Object>> queryEvents(UUID streamId, long fromVersion);

    default Future<Iterable<Object>> queryEvents(UUID streamId) {
        long fromVersion = 0;
        return queryEvents(streamId, fromVersion);
    }
}
