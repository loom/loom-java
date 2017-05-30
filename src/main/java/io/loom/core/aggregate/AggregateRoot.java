package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;
import java.util.UUID;

public interface AggregateRoot {
    UUID getId();

    Iterable<DomainEvent> pollAllPendingEvents();
}
