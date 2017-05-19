package io.loom.core.event;

import io.loom.core.aggregate.VersionedAggregate;

import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class AbstractDomainEvent implements LazyInitializedDomainEvent {
    private UUID aggregateId;
    private long version;
    private ZonedDateTime occurrenceTime;

    protected AbstractDomainEvent() {
    }

    protected AbstractDomainEvent(UUID aggregateId, long version, ZonedDateTime occurrenceTime) {
        this.checkHeaderPropertiesSet(aggregateId, version, occurrenceTime);
        this.aggregateId = aggregateId;
        this.version = version;
        this.occurrenceTime = occurrenceTime;
    }

    @Override
    public final void afterHeaderPropertiesSets(VersionedAggregate versionedAggregate) {
        if (this.isInitialized()) {
            throw new IllegalStateException("DomainEvent is already initialized.");
        }

        UUID aggregateId = versionedAggregate.getId();
        long version = versionedAggregate.getVersion() + 1;
        ZonedDateTime occurrenceTime = ZonedDateTime.now();
        this.checkHeaderPropertiesSet(aggregateId, version, occurrenceTime);

        this.aggregateId = aggregateId;
        this.version = version;
        this.occurrenceTime = occurrenceTime;
    }

    @Override
    public final boolean isInitialized() {
        return this.aggregateId != null
                && this.version > 0
                && this.occurrenceTime != null;
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

    private void checkHeaderPropertiesSet(
            UUID aggregateId, long version, ZonedDateTime occurrenceTime) {
        if (aggregateId == null) {
            throw new IllegalArgumentException("The parameter 'aggregateId' cannot be null.");
        }
        if (version < 1) {
            throw new IllegalArgumentException("The parameter 'version' must be greater than 0.");
        }
        if (occurrenceTime == null) {
            throw new IllegalArgumentException("The parameter 'occurrenceTime' cannot be null.");
        }
    }
}
