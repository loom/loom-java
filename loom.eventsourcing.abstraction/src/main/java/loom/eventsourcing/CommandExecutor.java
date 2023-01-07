package loom.eventsourcing;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

@FunctionalInterface
public interface CommandExecutor<S, C> {

    Iterable<Object> produceEvents(S state, C command);

    default Class<?> getCommandType() {
        return Arrays
            .stream(getClass().getGenericInterfaces())
            .map(i -> (ParameterizedType) i)
            .filter(p -> p.getRawType().equals(CommandExecutor.class))
            .map(p -> p.getActualTypeArguments()[1])
            .map(a -> (Class<?>) a)
            .findFirst()
            .get();
        }
}
