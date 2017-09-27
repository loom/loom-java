package io.loom.core.aggregate;

import io.loom.core.event.AbstractDomainEvent;
import io.loom.core.event.DomainEvent;

import java.time.ZonedDateTime;
import java.util.*;
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
        events.forEach(sut::raise);

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
        events.forEach(sut::raise);
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
        events.forEach(sut::raise);
        List<DomainEvent> result = (List<DomainEvent>) sut.pollAllPendingEvents();

        // Act
        DomainEvent newEvent = Mockito.mock(DomainEvent.class);
        result.add(newEvent);
    }

    @Test
    public void raise_adds_pending_event_correctly() {
        // Arrange
        AggregateRootProxy sut = new AggregateRootProxy(UUID.randomUUID());
        AbstractDomainEvent domainEvent = Mockito.spy(AbstractDomainEvent.class);
        ZonedDateTime lowerBoundOfOccurrenceTime = ZonedDateTime.now();

        // Act
        sut.raise(domainEvent);

        // Assert
        List<DomainEvent> actual = new ArrayList<>();
        sut.pollAllPendingEvents().forEach(actual::add);
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(domainEvent, actual.get(0));
        Assert.assertEquals(1, actual.get(0).getVersion());
        Assert.assertEquals(sut.getId(), actual.get(0).getAggregateId());
        Assert.assertTrue(lowerBoundOfOccurrenceTime.compareTo(actual.get(0).getOccurrenceTime()) <= 0);
        Assert.assertTrue(ZonedDateTime.now().compareTo(actual.get(0).getOccurrenceTime()) >= 0);
    }

    @Test
    public void raise_increases_version() {
        AggregateRootProxy sut = new AggregateRootProxy(UUID.randomUUID());
        sut.raise(Mockito.spy(AbstractDomainEvent.class));
        Assert.assertEquals(1, sut.getVersion());
    }

    @Test
    public void raise_appends_pending_event_correctly() {
        // Arrange
        AggregateRootProxy sut = new AggregateRootProxy(UUID.randomUUID());
        sut.raise(Mockito.spy(AbstractDomainEvent.class));
        AbstractDomainEvent domainEvent = Mockito.spy(AbstractDomainEvent.class);
        ZonedDateTime lowerBoundOfOccurrenceTime = ZonedDateTime.now();

        // Act
        sut.raise(domainEvent);

        // Assert
        List<DomainEvent> actual = new ArrayList<>();
        sut.pollAllPendingEvents().forEach(actual::add);
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(domainEvent, actual.get(1));
        Assert.assertEquals(2, actual.get(1).getVersion());
        Assert.assertEquals(sut.getId(), actual.get(1).getAggregateId());
        Assert.assertTrue(lowerBoundOfOccurrenceTime.compareTo(actual.get(1).getOccurrenceTime()) <= 0);
        Assert.assertTrue(ZonedDateTime.now().compareTo(actual.get(1).getOccurrenceTime()) >= 0);
    }

    @Test
    public void raise_has_guard_clause_against_null_domainEvent() {
        // Arrange
        AggregateRootProxy sut = new AggregateRootProxy(UUID.randomUUID());
        Exception thrown = null;

        // Act
        try {
            sut.raise(null);
        }
        catch (Exception exception) {
            thrown = exception;
        }

        // Assert
        Assert.assertNotNull(thrown);
        Assert.assertTrue(
                "The type of exception should be " + IllegalArgumentException.class.getTypeName()
                        + ", but the actual type is "
                        + thrown.getClass().getTypeName()
                        + ".",
                thrown instanceof IllegalArgumentException);
        IllegalArgumentException actual = (IllegalArgumentException)thrown;
        Assert.assertTrue(actual.getMessage().contains("domainEvent"));
    }

    public class AggregateRootProxy extends AbstractAggregateRoot {
        public AggregateRootProxy(UUID id) {
            super(id);
        }

        public void raise(DomainEvent domainEvent) {
            super.raise(domainEvent);
        }
    }
}
