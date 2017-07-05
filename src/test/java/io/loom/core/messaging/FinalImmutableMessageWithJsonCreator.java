package io.loom.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class FinalImmutableMessageWithJsonCreator implements Message {
    private final int intField;
    private final String stringField;

    @JsonCreator
    FinalImmutableMessageWithJsonCreator(
            @JsonProperty("intProperty") int intValue,
            @JsonProperty("stringProperty") String stringValue) {
        this.intField = intValue;
        this.stringField = stringValue;
    }

    public int getIntProperty() {
        return intField;
    }

    public String getStringProperty() {
        return stringField;
    }
}
