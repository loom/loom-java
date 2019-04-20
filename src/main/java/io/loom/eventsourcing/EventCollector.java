package io.loom.eventsourcing;

import java.util.UUID;
import java.util.concurrent.Future;

public interface EventCollector {
    Future<Void> collectEvents(UUID streamId, long firstVersion, Iterable<Object> events);
}
