package loom.eventsourcing;

@FunctionalInterface
public interface CommandExecutor<S, C> {

    Iterable<Object> produceEvents(S state, C command);
}
