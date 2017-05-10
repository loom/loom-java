package io.loom.core.event;

import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class AbstractDomainEvent implements DomainEvent {
    private final UUID aggregateId;
    private final long version;
    private final ZonedDateTime occurrenceTime;

    protected AbstractDomainEvent(UUID aggregateId, long version) {
        if (aggregateId == null) {
            throw new IllegalArgumentException("The parameter 'aggregateId' cannot be null.");
        }
        if (version < 1) {
            throw new IllegalArgumentException("The parameter 'version' must be greater than 0.");
        }

        this.aggregateId = aggregateId;
        this.version = version;
        this.occurrenceTime = ZonedDateTime.now();
    }

    @Override
    public final UUID getAggregateId() {
        return this.aggregateId;
    }

    @Override
    public final long getVersion() {
        return this.version;
    }

    @Override
    public final ZonedDateTime getOccurrenceTime() {
        return this.occurrenceTime;
    }
}
