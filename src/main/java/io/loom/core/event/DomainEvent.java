package io.loom.core.event;

import io.loom.core.entity.VersionedEntity;
import io.loom.core.messaging.Message;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface DomainEvent extends Message {
    UUID getAggregateId();

    long getVersion();

    ZonedDateTime getOccurrenceTime();

    void onRaise(VersionedEntity versionedEntity);
}
