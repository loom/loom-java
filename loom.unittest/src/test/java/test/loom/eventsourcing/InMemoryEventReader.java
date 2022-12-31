package test.loom.eventsourcing;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import loom.eventsourcing.EventReader;

public class InMemoryEventReader implements EventReader {

    private final Map<String, List<Object>> events = new HashMap<>();

    public void addEvent(String streamId, Object event) {
        List<Object> stream = events.getOrDefault(streamId, new ArrayList<>());
        stream.add(event);
        events.put(streamId, stream);
    }

    @Override
    public Iterable<Object> queryEvents(String streamId, long fromVersion) {
        List<Object> stream = events.getOrDefault(streamId, emptyList());
        int fromIndexInclusive = (int) fromVersion - 1;
        int toIndexExclusive = stream.size();
        return stream.subList(fromIndexInclusive, toIndexExclusive);
    }
}
