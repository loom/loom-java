package io.loom.eventsourcing;

public interface EventProducer<T> {
    Iterable<Object> produceEvents(T state, Object command);
}
