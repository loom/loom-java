package io.loom.eventsourcing.amazon;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.loom.eventsourcing.EventCollector;
import io.loom.eventsourcing.EventReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DynamoDBEventStore implements EventCollector, EventReader {
    private final DynamoDBMapper mapper;

    public DynamoDBEventStore(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Future<Void> collectEvents(
            UUID streamId, long firstVersion, Iterable<Object> events) {
        ArrayList<StreamEvent> streamEvents = createStreamEvents(streamId, firstVersion, events);
        saveStreamEvents(streamEvents);
        return CompletableFuture.completedFuture(null);
    }

    private ArrayList<StreamEvent> createStreamEvents(UUID streamId, long firstVersion, Iterable<Object> events) {
        ArrayList<StreamEvent> streamEvents = new ArrayList<>();
        long version = firstVersion;
        for (Object event : events) {
            streamEvents.add(createStreamEvent(streamId, version++, event));
        }
        return streamEvents;
    }

    private StreamEvent createStreamEvent(
            UUID streamId, long version, Object event) {
        StreamEvent streamEvent = new StreamEvent();

        streamEvent.setStreamId(streamId);
        streamEvent.setVersion(version);
        streamEvent.setEventType(event.getClass().getName());
        streamEvent.setEventData(serializeEvent(event));

        return streamEvent;
    }

    private String serializeEvent(Object event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            final String message = "Could not serialize the event object. Refer the cause for details.";
            throw new RuntimeException(message, exception);
        }
    }

    private void saveStreamEvents(ArrayList<StreamEvent> streamEvents) {
        mapper.batchSave(streamEvents);
    }

    @Override
    public Future<Iterable<Object>> queryEvents(UUID streamId, long fromVersion) {
        Iterable<StreamEvent> source = fetchStreamEvents(streamId, fromVersion);
        ArrayList<Object> events = deserializeEvents(source);
        return CompletableFuture.completedFuture(events);
    }

    private Iterable<StreamEvent> fetchStreamEvents(
            UUID streamId, long fromVersion) {
        final String expression = "StreamId = :streamId and Version >= :fromVersion";

        final HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":streamId", new AttributeValue().withS(streamId.toString()));
        values.put(":fromVersion", new AttributeValue().withN(Long.toString(fromVersion)));

        final DynamoDBQueryExpression<StreamEvent> queryExpression =
                new DynamoDBQueryExpression<StreamEvent>()
                        .withKeyConditionExpression(expression)
                        .withExpressionAttributeValues(values);

        return mapper.query(StreamEvent.class, queryExpression);
    }

    private ArrayList<Object> deserializeEvents(Iterable<StreamEvent> source) {
        ArrayList<Object> events = new ArrayList<>();
        for (StreamEvent s : source) {
            events.add(deserializeEvent(s.getEventType(), s.getEventData()));
        }
        return events;
    }

    private Object deserializeEvent(String eventType, String eventData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(eventData, Class.forName(eventType));
        } catch (IOException | ClassNotFoundException exception) {
            final String message = "Could not deserialize an event object. Refer the cause for details.";
            throw new RuntimeException(message, exception);
        }
    }
}
