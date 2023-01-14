package loom.eventsourcing;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Rehydrator<S> {

    private final Class<S> stateType;
    private final EventReader eventReader;
    private final Supplier<S> seedFactory;
    private final Map<Class<?>, EventHandler<S, Object>> eventHandlers;

    protected Rehydrator(
        EventReader eventReader,
        Supplier<S> seedFactory,
        Iterable<EventHandler<S, ?>> eventHandlers
    ) {
        this.stateType = getStateType(getClass());
        this.eventReader = eventReader;
        this.seedFactory = seedFactory;
        this.eventHandlers = toDictionary(eventHandlers);
    }

    protected Iterable<Class<?>> getEventTypes() {
        return eventHandlers.keySet();
    }

    protected Class<S> getStateType() {
        return stateType;
    }

    @SuppressWarnings("unchecked")
    private static <S> Class<S> getStateType(Class<?> type) {
        ParameterizedType generic = (ParameterizedType) type.getGenericSuperclass();
        return (Class<S>) generic.getActualTypeArguments()[0];
    }

    private static <E> Stream<E> stream(Iterable<E> source) {
        return StreamSupport.stream(source.spliterator(), false);
    }

    @SuppressWarnings("unchecked")
    private static <S> Map<Class<?>, EventHandler<S, Object>> toDictionary(
        Iterable<EventHandler<S, ?>> eventHandlers
    ) {
        return stream(eventHandlers)
            .map(h -> (EventHandler<S, Object>) h)
            .map(Rehydrator::failIfHandlerIsGeneric)
            .collect(toMap(h -> h.getEventType(), h -> h));
    }

    private static <S> EventHandler<S, Object> failIfHandlerIsGeneric(
        EventHandler<S, Object> handler
    ) {
        if (isGeneric(handler.getClass())) {
            String message = "Non-generic class expected for event handler."
                + " Type argument cannot be resolved."
                + " Make sure to specify the type argument"
                + " when declaring the event handler class.";
            throw new RuntimeException(message);
        }

        return handler;
    }

    private static boolean isGeneric(Class<?> type) {
        return type.getTypeParameters().length > 0;
    }

    public final Snapshot<S> rehydrateState(String streamId) {
        return foldl(
            this::handleEvent,
            Snapshot.seed(streamId, seedFactory.get()),
            stream(eventReader.queryEvents(stateType, streamId, 1)));
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
        EventHandler<S, Object> handler = getHandler(event);
        S nextState = handler.handleEvent(snapshot.getState(), event);
        return snapshot.next(nextState);
    }

    private EventHandler<S, Object> getHandler(Object event) {
        EventHandler<S, Object> handler = findHandler(event);
        if (handler == null) {
            throw new RuntimeException(
                "No event handler registered for event "
                + event.getClass().getName());
        }
        return handler;
    }

    private EventHandler<S, Object> findHandler(Object event) {
        return eventHandlers.getOrDefault(event.getClass(), null);
    }
}
