package io.loom.core.event;

import io.loom.core.entity.VersionedEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getAggregateId();

    long getVersion();

    ZonedDateTime getOccurrenceTime();

    void raise(VersionedEntity versionedEntity);
}
