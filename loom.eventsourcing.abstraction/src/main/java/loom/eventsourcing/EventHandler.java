package loom.eventsourcing;

public abstract class EventHandler<S, E> {

    public abstract S handleEvent(S state, E event);
}
