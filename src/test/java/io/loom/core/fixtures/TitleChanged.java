package io.loom.core.fixtures;

import io.loom.core.event.AbstractDomainEvent;

import java.beans.ConstructorProperties;
import java.time.ZonedDateTime;
import java.util.UUID;

public class TitleChanged extends AbstractDomainEvent {
    private final String title;

    @ConstructorProperties({"aggregateId", "version", "occurrenceTime", "title"})
    public TitleChanged(
            UUID aggregateId, long version, ZonedDateTime occurrenceTime, String title) {
        super(aggregateId, version, occurrenceTime);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
