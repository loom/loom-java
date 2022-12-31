package loom.eventsourcing;

public interface EventReader {

    Iterable<Object> queryEvents(String streamId, long fromVersion);
}
