package io.loom.core.event;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getAggregateId();

    long getVersion();

    ZonedDateTime getOccurrenceTime();
}
