package io.loom.core.fixtures;

import io.loom.core.aggregate.AggregateRoot;
import io.loom.core.event.DomainEvent;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class Issue implements AggregateRoot {
    private final List<DomainEvent> events = new ArrayList<>();
    private UUID id;
    private long version;
    private String title;
    private String content;

    Issue() {
    }

    public Issue(UUID id, String title, String body) {
        raise(new Issue.Created(id, 0, title, body));
    }

    public void changeTitle(String title) {
        raise(new Issue.TitleChanged(getId(), version + 1, title));
    }

    public void changeContent(String content) {
        raise(new Issue.ContentChanged(getId(), version + 1, content));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    private void raise(DomainEvent<Issue> event) {
        apply(event);
        events.add(event);
    }

    public void apply(DomainEvent<Issue> event) {
        event.applyTo(this);
    }

    private void applyIssueCreated(Issue.Created event) {
        this.id = event.getAggregateId();
        this.title = event.getTitle();
        this.content = event.getContent();
        this.version = event.getVersion();
    }

    private void applyIssueTitleChanged(Issue.TitleChanged event) {
        this.title = event.getTitle();
        this.version = event.getVersion();
    }

    private void applyIssueContentChanged(Issue.ContentChanged event) {
        this.content = event.getContent();
        this.version = event.getVersion();
    }

    @Override
    public Iterable<DomainEvent> pollAllPendingEvents() {
        List<DomainEvent> events = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(events);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Issue issue = (Issue) o;
        if (version != issue.version) {
            return false;
        }
        return id.equals(issue.id);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    public static class Created implements DomainEvent<Issue> {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String title;
        private final String content;

        public Created(UUID id, long version, String title, String content) {
            this.id = id;
            this.version = version;
            this.eventTime = ZonedDateTime.now();
            this.title = title;
            this.content = content;
        }

        @Override
        public UUID getAggregateId() {
            return id;
        }

        @Override
        public long getVersion() {
            return version;
        }

        @Override
        public ZonedDateTime getOccurrenceTime() {
            return eventTime;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        @Override
        public Issue applyTo(Issue aggregate) {
            aggregate.applyIssueCreated(this);
            return aggregate;
        }
    }

    public static class TitleChanged implements DomainEvent<Issue> {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String title;

        public TitleChanged(UUID id, long version, String title) {
            this.id = id;
            this.version = version;
            this.eventTime = ZonedDateTime.now();
            this.title = title;
        }

        @Override
        public UUID getAggregateId() {
            return id;
        }

        @Override
        public long getVersion() {
            return version;
        }

        @Override
        public ZonedDateTime getOccurrenceTime() {
            return eventTime;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public Issue applyTo(Issue aggregate) {
            aggregate.applyIssueTitleChanged(this);
            return aggregate;
        }
    }

    public static class ContentChanged implements DomainEvent<Issue> {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String content;

        public ContentChanged(UUID id, long version, String content) {
            this.id = id;
            this.version = version;
            this.eventTime = ZonedDateTime.now();
            this.content = content;
        }

        @Override
        public UUID getAggregateId() {
            return id;
        }

        @Override
        public long getVersion() {
            return version;
        }

        @Override
        public ZonedDateTime getOccurrenceTime() {
            return eventTime;
        }

        public String getContent() {
            return content;
        }

        @Override
        public Issue applyTo(Issue aggregate) {
            aggregate.applyIssueContentChanged(this);
            return aggregate;
        }
    }
}
