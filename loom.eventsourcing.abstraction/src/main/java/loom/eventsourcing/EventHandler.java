package loom.eventsourcing;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

@FunctionalInterface
public interface EventHandler<S, E> {

    S handleEvent(S state, E event);

    default Class<?> getEventType() {
        return Arrays
            .stream(getClass().getGenericInterfaces())
            .map(i -> (ParameterizedType) i)
            .filter(p -> p.getRawType().equals(EventHandler.class))
            .map(p -> p.getActualTypeArguments()[1])
            .map(a -> (Class<?>) a)
            .findFirst()
            .get();
    }
}
