package io.loom.core.aggregate;

import io.loom.core.entity.VersionedEntity;
import io.loom.core.event.DomainEvent;

public interface AggregateRoot extends VersionedEntity {
    Iterable<DomainEvent> pollAllPendingEvents();
}
