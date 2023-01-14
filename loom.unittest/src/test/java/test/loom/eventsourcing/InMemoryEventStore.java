package test.loom.eventsourcing;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import loom.eventsourcing.EventStore;

public class InMemoryEventStore implements EventStore {

    private final Map<Type, Map<String, List<Object>>> eventStore;

    public InMemoryEventStore() {
        eventStore = new ConcurrentHashMap<>();
    }

    @Override
    public Iterable<Object> queryEvents(
        Type stateType,
        String streamId,
        long fromVersion
    ) {
        Map<String, List<Object>> streams = eventStore.get(stateType);
        if (streams == null) {
            return emptyList();
        }

        List<Object> events = streams.get(streamId);
        if (events == null) {
            return emptyList();
        }

        int fromIndexInclusive = (int) fromVersion - 1;
        int toIndexExclusive = events.size();
        return events.subList(fromIndexInclusive, toIndexExclusive);
    }

    @Override
    public void collectEvents(
        Type stateType,
        String processId,
        String initiator,
        String predecessorId,
        String streamId,
        long startVersion,
        Iterable<Object> events
    ) {
        collectEvents(stateType, streamId, startVersion, events);
    }

    public void collectEvents(
        Type stateType,
        String streamId,
        Iterable<Object> events
    ) {
        int startVersion = 1;
        collectEvents(stateType, streamId, startVersion, events);
    }

    public void collectEvents(
        Type stateType,
        String streamId,
        long startVersion,
        Iterable<Object> events
    ) {
        Map<String, List<Object>> streams = eventStore.computeIfAbsent(
            stateType,
            k -> new ConcurrentHashMap<>());

        List<Object> stream = streams.computeIfAbsent(
            streamId,
            k -> new ArrayList<>());

        if (stream.size() + 1 != startVersion) {
            throw new IllegalStateException(
                "Invalid start version: expected "
                + (stream.size() + 1)
                + ", got " + startVersion);
        }

        for (Object event : events) {
            stream.add(event);
        }
    }
}
