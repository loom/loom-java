package loom.eventsourcing;

import java.lang.reflect.Type;

public interface EventCollector {

    void collectEvents(
        Type stateType,
        String processId,
        String initiator,
        String predecessorId,
        String streamId,
        long startVersion,
        Iterable<Object> events);
}
