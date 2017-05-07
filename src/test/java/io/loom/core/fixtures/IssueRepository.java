package io.loom.core.fixtures;

import io.loom.core.aggregate.Repository;
import io.loom.core.event.DomainEvent;
import io.loom.core.store.EventStore;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class IssueRepository implements Repository<Issue> {
    private final EventStore eventStore;

    public IssueRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(Issue issue) {
        eventStore.saveEvents(issue.getId(), issue.pollAllPendingEvents());
    }

    @Override
    public Optional<Issue> load(UUID id) {
        Iterable<DomainEvent> events = eventStore.getEvents(id);
        if (!events.iterator().hasNext()) {
            return Optional.empty();
        }
        Issue issue = new Issue();
        events.forEach(issue::apply);
        return Optional.of(issue);
    }
}
