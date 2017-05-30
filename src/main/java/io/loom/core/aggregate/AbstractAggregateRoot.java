package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;
import io.loom.core.event.LazyInitializedDomainEvent;

import java.util.UUID;

public abstract class AbstractAggregateRoot implements AggregateRoot, VersionedAggregate {
    private final UUID id;
    private long version;

    protected AbstractAggregateRoot(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("The parameter 'id' cannot be null.");
        }

        this.id = id;
        this.version = 0;
    }

    protected final void raise(DomainEvent domainEvent) {
        if (domainEvent == null) {
            throw new IllegalArgumentException("The parameter 'domainEvent' cannot be null.");
        }

        if (domainEvent instanceof LazyInitializedDomainEvent) {
            LazyInitializedDomainEvent lazyEvent = (LazyInitializedDomainEvent) domainEvent;
            if (!lazyEvent.isInitialized()) {
                lazyEvent.afterHeaderPropertiesSets(this);
            }
        }

        // TODO: guard clause domain event header properties sets
        // TODO: apply and pending
    }

    @Override
    public final UUID getId() {
        return this.id;
    }

    @Override
    public final long getVersion() {
        return version;
    }

    @Override
    public final Iterable<DomainEvent> pollAllPendingEvents() {
        return null;
    }
}
