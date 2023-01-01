package test.loom.eventsourcing;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import loom.eventsourcing.EventReader;

public class InMemoryEventReader implements EventReader {

    private final Map<Type, Map<String, List<Object>>> eventStore;

    public InMemoryEventReader() {
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

    public void addEvent(Type stateType, String streamId, Object event) {
        Map<String, List<Object>> streams = eventStore.computeIfAbsent(
            stateType,
            k -> new ConcurrentHashMap<>());

        List<Object> stream = streams.computeIfAbsent(
            streamId,
            k -> new ArrayList<>());

        stream.add(event);
    }
}
