package io.loom.core.event;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface DomainEvent extends Serializable {
    UUID getAggregateId();

    long getVersion();

    ZonedDateTime getOccurrenceTime();
}
