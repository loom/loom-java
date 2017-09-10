package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractAggregateRootSpecs {
    public class IssueForTesting extends AbstractAggregateRoot {
        public IssueForTesting(UUID id) {
            super(id);
        }
    }

    @Test
    public void constructor_has_guard_clause_for_null_id() {
        // Arrange
        UUID id = null;

        // Act
        IllegalArgumentException expected = null;
        try {
            new IssueForTesting(id);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'id'.",
                expected.getMessage().contains("'id'"));
    }

    @Test
    public void constructor_sets_id_correctly() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        IssueForTesting sut = new IssueForTesting(id);

        // Assert
        Assert.assertEquals(id, sut.getId());
    }

    @Test
    public void constructor_sets_version_to_zero() {
        // Arrange

        // Act
        IssueForTesting sut = new IssueForTesting(UUID.randomUUID());

        // Assert
        Assert.assertEquals(0, sut.getVersion());
    }

    @Test
    public void pollAllPendingEvents_returns_new_Iterable_instance() {
        // Arrange
        IssueForTesting sut = new IssueForTesting(UUID.randomUUID());

        // Act
        Iterable<DomainEvent> result = sut.pollAllPendingEvents();

        // Assert
        Assert.assertNotNull(result);
        Assert.assertFalse(result.iterator().hasNext());
    }

    @Test
    public void pollAllPendingEvents_returns_all_pending_events() {
        // Arrange
        IssueForTesting sut = new IssueForTesting(UUID.randomUUID());
        int count = new Random().nextInt(100) + 1;
        List<DomainEvent> events = Stream.iterate(0, i -> i  + 1).limit(count)
                .map(i -> Mockito.mock(DomainEvent.class))
                .collect(Collectors.toList());
        sut.pendingEvents.addAll(events);

        // Act
        Iterable<DomainEvent> result = sut.pollAllPendingEvents();

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(events, result);
    }

    @Test
    public void pollAllPendingEvents_clears_pending_event_queue() {
        // Arrange
        IssueForTesting sut = new IssueForTesting(UUID.randomUUID());
        int count = new Random().nextInt(100) + 1;
        List<DomainEvent> events = Stream.iterate(0, i -> i  + 1).limit(count)
                .map(i -> Mockito.mock(DomainEvent.class))
                .collect(Collectors.toList());
        sut.pendingEvents.addAll(events);
        sut.pollAllPendingEvents();

        // Act
        Iterable<DomainEvent> result = sut.pollAllPendingEvents();

        // Assert
        Assert.assertNotNull(result);
        Assert.assertFalse(result.iterator().hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pollAllPendingEvents_returns_unmodifiable_collection() {
        // Arrange
        IssueForTesting sut = new IssueForTesting(UUID.randomUUID());
        int count = new Random().nextInt(100) + 1;
        List<DomainEvent> events = Stream.iterate(0, i -> i  + 1).limit(count)
                .map(i -> Mockito.mock(DomainEvent.class))
                .collect(Collectors.toList());
        sut.pendingEvents.addAll(events);
        List<DomainEvent> result = (List<DomainEvent>) sut.pollAllPendingEvents();

        // Act
        DomainEvent newEvent = Mockito.mock(DomainEvent.class);
        result.add(newEvent);
    }
}
