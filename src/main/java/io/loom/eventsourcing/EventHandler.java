package io.loom.eventsourcing;

public interface EventHandler<T> {
    T handleEvents(T state, Iterable<Object> events);
}
