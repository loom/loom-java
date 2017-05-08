package io.loom.core.store;

import io.loom.core.aggregate.AggregateRoot;
import io.loom.core.event.DomainEvent;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface EventStore<AggregateT extends AggregateRoot> {
    void saveEvents(UUID id, Iterable<DomainEvent<AggregateT>> events);

    Iterable<DomainEvent<AggregateT>> getEvents(UUID id);
}
