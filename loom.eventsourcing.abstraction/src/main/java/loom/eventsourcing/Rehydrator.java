package loom.eventsourcing;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Rehydrator<S> {

    private final Supplier<S> seedFactory;
    private final EventReader eventReader;
    private final List<EventHandler<S, ?>> eventHandlers;

    public Rehydrator(
        Supplier<S> seedFactory,
        EventReader eventReader,
        Iterable<EventHandler<S, ?>> eventHandlers
    ) {
        this.seedFactory = seedFactory;
        this.eventReader = eventReader;
        this.eventHandlers = toUnmodifiableList(eventHandlers);
    }

    private static <E> List<E> toUnmodifiableList(Iterable<E> eventHandlers) {
        return stream(eventHandlers).collect(Collectors.toList());
    }

    private static <E> Stream<E> stream(Iterable<E> eventHandlers) {
        return StreamSupport.stream(eventHandlers.spliterator(), false);
    }

    public final Snapshot<S> rehydrateState(String streamId) {
        return foldl(
            this::handleEvent,
            new Snapshot<S>(streamId, 0, seedFactory.get()),
            stream(eventReader.queryEvents(streamId, 1)));
    }

    private static <T, U> U foldl(BiFunction<U, T, U> f, U z, Stream<T> xs) {
        Iterator<T> i = xs.iterator();
        U a = z;
        while (i.hasNext()) {
            a = f.apply(a, i.next());
        }
        return a;
    }

    private Snapshot<S> handleEvent(Snapshot<S> snapshot, Object event) {
        for (EventHandler<S, ?> handler : eventHandlers) {
            Class<?> eventType = getEventType(handler);
            if (eventType.isInstance(event)) {
                S state = invokeHandler(handler, snapshot.getState(), event);
                return next(snapshot, state);
            }
        }

        throw new RuntimeException(
            "No event handler registered for event "
            + event.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private S invokeHandler(EventHandler<S, ?> handler, S state, Object event) {
        return ((EventHandler<S, Object>) handler).handleEvent(state, event);
    }

    private Class<?> getEventType(EventHandler<S, ?> handler) {
        ParameterizedType handlerType = getHandlerType(handler);
        return (Class<?>) handlerType.getActualTypeArguments()[1];
    }

    private ParameterizedType getHandlerType(EventHandler<S, ?> handler) {
        return (ParameterizedType) handler.getClass().getGenericSuperclass();
    }

    private Snapshot<S> next(Snapshot<S> snapshot, S state) {
        return new Snapshot<S>(
            snapshot.getStreamId(),
            snapshot.getVersion() + 1,
            state);
    }
}
