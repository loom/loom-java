package io.loom.core.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.loom.core.event.AbstractDomainEvent;
import io.loom.core.event.DomainEvent;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JacksonMessageSerializerTest {
    private ObjectMapper mapper;

    /**
     * Sets up.
     */
    @Before
    public void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@type");
    }

    @Test
    public void jackson_serialize_include_type() throws IOException {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        ZonedDateTime occurrenceTime = ZonedDateTime.now();
        String title = "issue-title";
        TitleChanged event = new TitleChanged(aggregateId, version, occurrenceTime, title);

        // Act
        String sut = mapper.writeValueAsString(event);

        // Assert
        Assert.assertTrue(sut.contains(
                "\"@type\" : \"io.loom.core.message.JacksonMessageSerializerTest$TitleChanged"));
        Assert.assertTrue(sut.contains("\"aggregateId\" : \"" + aggregateId.toString() + "\""));
        Assert.assertTrue(sut.contains("\"version\" : " + 1));
        Assert.assertTrue(sut.contains("\"occurrenceTime\" : " + occurrenceTime.toEpochSecond()));
        Assert.assertTrue(sut.contains("\"title\" : " + "\"" + title + "\""));
    }

    @Test
    public void jackson_deserialize_with_type() throws IOException {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        ZonedDateTime occurrenceTime = ZonedDateTime.now();
        String title = "issue-title";
        TitleChanged event = new TitleChanged(aggregateId, version, occurrenceTime, title);
        String json = mapper.writeValueAsString(event);

        // Act
        Object sut = mapper.readValue(json, Object.class);

        // Assert
        Assert.assertTrue(sut instanceof DomainEvent);
        Assert.assertTrue(sut instanceof TitleChanged);

        TitleChanged deserialized = (TitleChanged) sut;
        Assert.assertEquals(aggregateId, deserialized.getAggregateId());
        Assert.assertEquals(version, deserialized.getVersion());
        Assert.assertEquals(
                occurrenceTime.toEpochSecond(), deserialized.getOccurrenceTime().toEpochSecond());
        Assert.assertEquals(title, deserialized.getTitle());
    }

    static class TitleChanged extends AbstractDomainEvent {
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
}
