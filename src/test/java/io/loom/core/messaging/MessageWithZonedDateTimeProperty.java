package io.loom.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class MessageWithZonedDateTimeProperty {
    private ZonedDateTime dateTime;

    @JsonCreator
    public MessageWithZonedDateTimeProperty(@JsonProperty("dateTime") ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ZonedDateTime getDateTime() {
        return this.dateTime;
    }
}
