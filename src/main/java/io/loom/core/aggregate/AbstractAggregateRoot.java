package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractAggregateRoot implements AggregateRoot {
    // Test 를 위해 package-public 으로 합니다.
    // TODO: raise method 가 구현될 때 private 으로 전환 합니다.
    final List<DomainEvent> pendingEvents = new ArrayList<>();

    private final UUID id;

    protected AbstractAggregateRoot(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("The parameter 'id' cannot be null.");
        }

        this.id = id;
    }

    @Override
    public final UUID getId() {
        return this.id;
    }

    @Override
    public final long getVersion() {
        return 0;
    }

    @Override
    public final Iterable<DomainEvent> pollAllPendingEvents() {
        if (this.pendingEvents.isEmpty()) {
            return Collections.emptyList();
        }

        List<DomainEvent> events = new ArrayList<>(this.pendingEvents);
        events = Collections.unmodifiableList(events);
        this.pendingEvents.clear();
        return events;
    }
}
