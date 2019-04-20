package io.loom.eventsourcing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ConventionalEventHandler<T> implements EventHandler<T> {
    private final Class<T> stateType;
    private final Map<Class<?>, Method> handlers;

    protected ConventionalEventHandler(Class<T> stateType) {
        this.stateType = stateType;
        this.handlers = getHandlers(stateType);
    }

    private Map<Class<?>, Method> getHandlers(Class<T> stateType) {
        HashMap<Class<?>, Method> handlers = new HashMap<>();

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals("handleEvent") == false) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 2) {
                continue;
            }

            Class<?> stateParamType = parameterTypes[0];
            if (stateParamType.equals(stateType) == false) {
                continue;
            }

            Class<?> eventParamType = parameterTypes[1];
            method.setAccessible(true);
            handlers.put(eventParamType, method);
        }

        return handlers;
    }

    @Override
    public T handleEvents(T state, Iterable<Object> events) {
        for (Object event : events) {
            state = handleEvent(state, event);
        }
        return state;
    }

    private T handleEvent(T state, Object event) {
        Method handler = getHandler(event.getClass());
        return stateType.cast(invokeHandler(handler, state, event));
    }

    private Method getHandler(Class<?> eventType) {
        Method handler = handlers.get(eventType);
        if (handler == null) {
            final String message = "Cannot handle the event of type " + eventType.getName() + ".";
            throw new RuntimeException(message);
        }
        return handler;
    }

    private Object invokeHandler(Method handler, T state, Object event) {
        try {
            return handler.invoke(this, state, event);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            final String message = "Cannot invoke the event handler method for the event of type " + event.getClass().getName() + ".";
            throw new RuntimeException(message, exception);
        }
    }
}
