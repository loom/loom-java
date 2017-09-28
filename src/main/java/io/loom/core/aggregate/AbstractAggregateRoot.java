package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractAggregateRoot implements AggregateRoot {
    private final UUID id;
    private final List<DomainEvent> pendingEvents = new ArrayList<>();
    private long version;

    protected AbstractAggregateRoot(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("The argument 'id' cannot be null.");
        }

        this.id = id;
        this.version = 0;
    }

    @Override
    public final UUID getId() {
        return this.id;
    }

    @Override
    public final long getVersion() {
        return this.version;
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

    protected void raise(DomainEvent domainEvent) {
        if (domainEvent == null) {
            throw new IllegalArgumentException("The argument 'domainEvent' cannot be null. ");
        }

        raiseAndAppend(domainEvent);
        increaseVersion();

        // TODO: 이벤트 처리기 실행 논리를 구현합니다.
    }

    private void raiseAndAppend(DomainEvent domainEvent) {
        domainEvent.onRaise(this);
        this.pendingEvents.add(domainEvent);
    }

    private void increaseVersion() {
        this.version++;
    }
}
