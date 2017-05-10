package io.loom.core.event;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class AbstractDomainEventSpecs {
    public class IssueCreatedForTesting extends AbstractDomainEvent {
        public IssueCreatedForTesting(
                UUID aggregateId, long version, ZonedDateTime occurrenceTime) {
            super(aggregateId, version, occurrenceTime);
        }
    }

    @Test
    public void constructor_has_guard_clause_for_null_aggregateId() {
        // Arrange
        UUID aggregateId = null;

        // Act
        IllegalArgumentException expected = null;
        try {
            new IssueCreatedForTesting(aggregateId, 1, ZonedDateTime.now());
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'aggregateId'.",
                expected.getMessage().contains("'aggregateId'"));
    }

    @Test
    public void constructor_has_guard_clause_for_minimum_value_of_version() {
        // Arrange
        long version = 0;

        // Act
        IllegalArgumentException expected = null;
        try {
            new IssueCreatedForTesting(UUID.randomUUID(), version, ZonedDateTime.now());
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'version'.",
                expected.getMessage().contains("'version'"));
    }

    @Test
    public void constructor_has_guard_clause_for_null_occurrenceTime() {
        // Arrange
        ZonedDateTime occurrenceTime = null;

        // Act
        IllegalArgumentException expected = null;
        try {
            new IssueCreatedForTesting(UUID.randomUUID(), 1, occurrenceTime);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'occurrenceTime'.",
                expected.getMessage().contains("'occurrenceTime'"));
    }

    @Test
    public void constructor_sets_header_properties_correctly() {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        Random random = new Random();
        long version = random.nextInt(Integer.MAX_VALUE) + 1L;
        ZonedDateTime occurrenceTime = ZonedDateTime.now().plusNanos(random.nextInt());

        // Act
        IssueCreatedForTesting sut = new IssueCreatedForTesting(
                aggregateId, version, occurrenceTime);

        // Assert
        Assert.assertEquals(aggregateId, sut.getAggregateId());
        Assert.assertEquals(version, sut.getVersion());
        Assert.assertEquals(occurrenceTime, sut.getOccurrenceTime());
    }
}
