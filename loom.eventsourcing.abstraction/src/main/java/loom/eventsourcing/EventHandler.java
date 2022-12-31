package loom.eventsourcing;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class EventHandler<S, E> {

    private final Class<S> stateType;
    private final Class<E> eventType;

    public abstract S handleEvent(S state, E event);
}
