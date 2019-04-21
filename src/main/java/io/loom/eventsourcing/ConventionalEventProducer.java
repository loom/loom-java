package io.loom.eventsourcing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ConventionalEventProducer<T> implements EventProducer<T> {
    private final Map<Class<?>, Method> producers;

    protected ConventionalEventProducer(Class<T> stateType) {
        this.producers = getProducers(stateType);
    }

    private Map<Class<?>, Method> getProducers(Class<T> stateType) {
        HashMap<Class<?>, Method> producers = new HashMap<>();

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("produceEvents") == false) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 2) {
                continue;
            }

            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Iterable.class) == false) {
                continue;
            }

            Class<?> stateParamType = parameterTypes[0];
            if (stateParamType.equals(stateType) == false) {
                continue;
            }

            Class<?> commandParamType = parameterTypes[1];
            method.setAccessible(true);
            producers.put(commandParamType, method);
        }

        return producers;
    }

    @Override
    public Iterable<Object> produceEvents(T state, Object command) {
        final Method producer = getProducer(command.getClass());
        // TODO: Revolve the warning.
        return (Iterable<Object>)invokeProducer(producer, state, command);
    }

    private Method getProducer(Class<?> commandType) {
        final Method producer = producers.get(commandType);
        if (producer == null) {
            final String message = "Cannot execute the command of type " + commandType.getName() + ".";
            throw new RuntimeException(message);
        }
        return producer;
    }

    private Object invokeProducer(Method producer, T state, Object command) {
        try {
            return producer.invoke(this, state, command);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            final String message = "Cannot invoke the command producer method for the command of type " + command.getClass().getName() + ".";
            throw new RuntimeException(message, exception);
        }
    }
}
