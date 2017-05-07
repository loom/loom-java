package io.loom.core.fixtures;

import io.loom.core.event.DomainEvent;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 7..
 */
public class UnknownEvent implements DomainEvent {
    private final UUID aggregateId;
    private final long version;
    private final ZonedDateTime occurrenceTime;

    /**
     * Instantiates a new Unknown event.
     *
     * @param aggregateId the aggregate id
     * @param version     the version
     */
    public UnknownEvent(UUID aggregateId, long version) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.occurrenceTime = ZonedDateTime.now();
    }

    @Override
    public UUID getAggregateId() {
        return aggregateId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public ZonedDateTime getOccurrenceTime() {
        return occurrenceTime;
    }
}
