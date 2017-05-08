package io.loom.core.fixtures;

import io.loom.core.event.DomainEvent;
import io.loom.core.store.EventStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class InMemoryEventStore implements EventStore<Issue> {
    private final Map<UUID, List<DomainEvent<Issue>>> store = new ConcurrentHashMap<>();

    @Override
    public void saveEvents(UUID uuid, Iterable<DomainEvent<Issue>> domainEvents) {
        if (store.containsKey(uuid)) {
            List<DomainEvent<Issue>> events = store.get(uuid);
            domainEvents.forEach(events::add);
        } else {
            List<DomainEvent<Issue>> events = new ArrayList<>();
            domainEvents.forEach(events::add);
            store.put(uuid, events);
        }
    }

    @Override
    public Iterable<DomainEvent<Issue>> getEvents(UUID uuid) {
        return store.getOrDefault(uuid, Collections.emptyList());
    }
}
