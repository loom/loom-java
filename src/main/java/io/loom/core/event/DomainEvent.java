package io.loom.core.event;

import io.loom.core.aggregate.AggregateRoot;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface DomainEvent<AggregateT extends AggregateRoot> extends Serializable {
    UUID getAggregateId();

    long getVersion();

    ZonedDateTime getOccurrenceTime();

    AggregateT applyTo(AggregateT aggregate);
}
