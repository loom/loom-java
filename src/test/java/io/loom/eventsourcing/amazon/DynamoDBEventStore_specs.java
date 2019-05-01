package io.loom.eventsourcing.amazon;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static io.loom.eventsourcing.amazon.LocalDynamoDB.getMapper;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DynamoDBEventStore_specs {
    private static final Random random = new Random();

    public static class Event1 {
        private final int value;

        @JsonCreator
        Event1(@JsonProperty("value") int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Event2 {
        private final double value;

        @JsonCreator
        Event2(@JsonProperty("value") double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    public static class Event3 {
        private final String value;

        @JsonCreator
        Event3(@JsonProperty("value") String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void queryEvents_restores_events_correctly()
            throws InterruptedException, ExecutionException {
        // Arrange
        final DynamoDBEventStore sut = new DynamoDBEventStore(getMapper());

        UUID streamId = randomUUID();
        long firstVersion = 1;
        final List<Object> events = asList(
                new Event3(randomUUID().toString()),
                new Event1(random.nextInt()),
                new Event2(random.nextDouble()));

        sut.collectEvents(streamId, firstVersion, events).get();

        // Act
        final Iterable<Object> actual = sut.queryEvents(streamId).get();

        // Assert
        assertThat(actual)
                .usingFieldByFieldElementComparator()
                .containsExactlyElementsOf(events);
    }

    @Test
    public void queryEvents_filters_events_by_stream_id()
            throws ExecutionException, InterruptedException {
        // Arrange
        final DynamoDBEventStore sut = new DynamoDBEventStore(getMapper());

        UUID streamId = randomUUID();
        long firstVersion = 1;
        final List<Object> events = asList(
                new Event3(randomUUID().toString()),
                new Event1(random.nextInt()),
                new Event2(random.nextDouble()));

        sut.collectEvents(streamId, firstVersion, events).get();

        sut.collectEvents(randomUUID(), firstVersion, events).get();

        // Act
        final Iterable<Object> actual = sut.queryEvents(streamId).get();

        // Assert
        assertThat(actual)
                .usingFieldByFieldElementComparator()
                .containsExactlyElementsOf(events);
    }

    @Test
    public void queryEvents_filters_events_by_version()
            throws ExecutionException, InterruptedException {
        // Arrange
        final DynamoDBEventStore sut = new DynamoDBEventStore(getMapper());

        UUID streamId = randomUUID();
        long firstVersion = 1;
        final List<Object> events = asList(
                new Event3(randomUUID().toString()),
                new Event1(random.nextInt()),
                new Event2(random.nextDouble()));

        sut.collectEvents(streamId, firstVersion, events).get();

        long fromVersion = 2;

        // Act
        final Iterable<Object> actual = sut.queryEvents(streamId, fromVersion).get();

        // Assert
        assertThat(actual)
                .usingFieldByFieldElementComparator()
                .containsExactlyElementsOf(events.stream().skip(1).collect(toList()));
    }

    @Test
    public void collectEvents_controls_concurrency()
            throws ExecutionException, InterruptedException {
        // Arrange
        final DynamoDBEventStore sut = new DynamoDBEventStore(getMapper());

        final UUID streamId = randomUUID();
        final long firstVersion = 1;
        final List<Object> events = singletonList(new Event1(random.nextInt()));

        sut.collectEvents(streamId, firstVersion, events).get();

        // Act
        final ThrowableAssert.ThrowingCallable action =
                () -> sut.collectEvents(streamId, firstVersion, events).get();

        // Assert
        assertThatThrownBy(action);
    }
}
