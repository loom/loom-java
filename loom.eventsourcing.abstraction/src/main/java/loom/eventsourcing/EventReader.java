package loom.eventsourcing;

import java.lang.reflect.Type;

public interface EventReader {

    Iterable<Object> queryEvents(
        Type stateType,
        String streamId,
        long fromVersion);
}
