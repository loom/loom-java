package io.loom.eventsourcing;

import java.util.UUID;
import java.util.concurrent.Future;

public interface EventReader {
    Future<Iterable<Object>> queryEvents(UUID streamId, long fromVersion);
}
