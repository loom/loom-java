package io.loom.core.exception;

import io.loom.core.aggregate.AggregateRoot;
import io.loom.core.event.DomainEvent;

/**
 * Created by mhyeon.lee on 2017. 5. 7..
 */
public class EventHandlerNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Event handler not found exception.
     *
     * @param aggregateType the aggregate type
     * @param eventType     the event type
     * @param throwable     the throwable
     */
    public EventHandlerNotFoundException(
            Class<? extends AggregateRoot> aggregateType,
            Class<? extends DomainEvent> eventType,
            Throwable throwable) {
        super("Could not find handler for apply event. "
                + "- AggregateRootType: " + aggregateType.getName()
                + ", DomainEventType: " + eventType.getName(),
                throwable);
    }
}
