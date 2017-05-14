package io.loom.core.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.loom.core.event.DomainEvent;
import io.loom.core.fixtures.TitleChanged;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.StringJoiner;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class JacksonMessageSerializerTest {
    private final MessageSerializer messageSerializer = new JacksonMessageSerializer();

    @Test
    public void jackson_serialize_include_type() throws IOException {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        ZonedDateTime occurrenceTime = ZonedDateTime.now();
        String title = "issue-title";
        TitleChanged event = new TitleChanged(aggregateId, version, occurrenceTime, title);

        // Act
        String sut = messageSerializer.serialize(event);

        // Assert
        Assert.assertTrue(sut.contains("\"@type\" : \"io.loom.core.fixtures.TitleChanged\""));
        Assert.assertTrue(sut.contains("\"aggregateId\" : \"" + aggregateId.toString() + "\""));
        Assert.assertTrue(sut.contains("\"version\" : " + version));
        Assert.assertTrue(sut.contains(
                "\"occurrenceTime\" : " + occurrenceTime.toInstant().toEpochMilli()));
        Assert.assertTrue(sut.contains("\"title\" : " + "\"" + title + "\""));
    }

    @Test
    public void jackson_deserialize_with_type() throws IOException {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;
        long occurrenceTime = ZonedDateTime.now().toInstant().toEpochMilli();
        String title = "issue-title";
        String json = "{\n  "
                + new StringJoiner(",\n  ")
                .add("\"@type\" : \"io.loom.core.fixtures.TitleChanged\"")
                .add("\"aggregateId\" : \"" + aggregateId.toString() + "\"")
                .add("\"version\" : " + version)
                .add("\"occurrenceTime\" : " + occurrenceTime)
                .add("\"title\" : " + "\"" + title + "\"")
                .toString()
                + "\n}";

        // Act
        Object sut = messageSerializer.deserialize(json);

        // Assert
        Assert.assertTrue(sut instanceof DomainEvent);
        Assert.assertTrue(sut instanceof TitleChanged);

        TitleChanged deserialized = (TitleChanged) sut;
        Assert.assertEquals(aggregateId, deserialized.getAggregateId());
        Assert.assertEquals(version, deserialized.getVersion());
        Assert.assertEquals(
                occurrenceTime, deserialized.getOccurrenceTime().toInstant().toEpochMilli());
        Assert.assertEquals(title, deserialized.getTitle());
    }

    private static class JacksonMessageSerializer implements MessageSerializer {
        private final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@type");

        @Override
        public String serialize(Object message) {
            try {
                return mapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object deserialize(String value) {
            try {
                return mapper.readValue(value, Object.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
