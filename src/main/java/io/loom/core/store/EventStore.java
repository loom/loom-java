package io.loom.core.store;

import io.loom.core.event.DomainEvent;

import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface EventStore {
    void saveEvents(UUID id, Iterable<DomainEvent> events);

    Iterable<DomainEvent> getEvents(UUID id);
}
