package io.loom.core.fixtures;

import io.loom.core.event.DomainEvent;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface IssueEvent extends DomainEvent {

    class IssueCreated implements IssueEvent {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String title;
        private final String content;

        public IssueCreated(UUID id, long version, String title, String content) {
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
        public ZonedDateTime getEventTime() {
            return eventTime;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    class IssueTitleChanged implements IssueEvent {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String title;

        public IssueTitleChanged(UUID id, long version, String title) {
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
        public ZonedDateTime getEventTime() {
            return eventTime;
        }

        public String getTitle() {
            return title;
        }
    }

    class IssueContentChanged implements IssueEvent {
        private final UUID id;
        private final long version;
        private final ZonedDateTime eventTime;
        private final String content;

        public IssueContentChanged(UUID id, long version, String content) {
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
        public ZonedDateTime getEventTime() {
            return eventTime;
        }

        public String getContent() {
            return content;
        }
    }
}
