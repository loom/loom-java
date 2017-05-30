package io.loom.core.event;

import io.loom.core.aggregate.VersionedAggregate;

public interface LazyInitializedDomainEvent extends DomainEvent {
    boolean isInitialized();

    void afterHeaderPropertiesSets(VersionedAggregate versionedAggregate);
}
