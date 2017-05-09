package io.loom.core.event;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class AbstractDomainEventSpecs {
    public class IssueCreatedForTesting extends AbstractDomainEvent {
        public IssueCreatedForTesting(UUID aggregateId, long version) {
            super(aggregateId, version);
        }
    }

    @Test
    public void constructor_has_guard_clause_for_null_aggregateId() {
        // Arrange
        UUID aggregateId = null;

        // Act
        IllegalArgumentException expected = null;
        try {
            new IssueCreatedForTesting(aggregateId, 1);
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
            new IssueCreatedForTesting(UUID.randomUUID(), version);
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
    public void constructor_sets_aggregateId_and_version_correctly() {
        // Arrange
        UUID aggregateId = UUID.randomUUID();
        long version = 1;

        // Act
        IssueCreatedForTesting sut = new IssueCreatedForTesting(aggregateId, version);

        // Assert
        Assert.assertEquals(aggregateId, sut.getAggregateId());
        Assert.assertEquals(version, sut.getVersion());
    }

    @Test
    public void constructor_sets_occurrenceTime_to_now() {
        // Arrange
        ZonedDateTime before = ZonedDateTime.now();

        // Act
        IssueCreatedForTesting sut = new IssueCreatedForTesting(UUID.randomUUID(), 1);

        // Assert
        ZonedDateTime after = ZonedDateTime.now();
        Assert.assertTrue(before.toEpochSecond() <= sut.getOccurrenceTime().toEpochSecond());
        Assert.assertTrue(after.toEpochSecond() >= sut.getOccurrenceTime().toEpochSecond());
    }
}
