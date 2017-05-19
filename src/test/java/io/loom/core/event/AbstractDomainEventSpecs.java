package io.loom.core.event;

import io.loom.core.aggregate.VersionedAggregate;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class AbstractDomainEventSpecs {
    public class IssueCreatedForTesting extends AbstractDomainEvent {
        public IssueCreatedForTesting() {
        }

        public IssueCreatedForTesting(
                UUID aggregateId, long version, ZonedDateTime occurrenceTime) {
            super(aggregateId, version, occurrenceTime);
        }
    }

    public class VersionedIssueForTesting implements VersionedAggregate {
        private final UUID id;
        private long version;

        public VersionedIssueForTesting(UUID id, long version) {
            this.id = id;
            this.version = version;
        }

        @Override
        public UUID getId() {
            return this.id;
        }

        @Override
        public long getVersion() {
            return this.version;
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

    @Test
    public void isInitialized_by_constructor_true() {
        // Arrange
        IssueCreatedForTesting domainEvent
                = new IssueCreatedForTesting(UUID.randomUUID(), 1, ZonedDateTime.now());

        // Act
        boolean sut = domainEvent.isInitialized();

        // Assert
        Assert.assertTrue(sut);
    }

    @Test
    public void isInitialized_by_no_args_constructor_false() {
        // Arrange
        IssueCreatedForTesting domainEvent = new IssueCreatedForTesting();

        // Act
        boolean sut = domainEvent.isInitialized();

        // Assert
        Assert.assertFalse(sut);
    }

    @Test
    public void isInitialized_by_afterHeaderPropertiesSets_true() {
        // Arrange
        VersionedAggregate versionedAggregate
                = new VersionedIssueForTesting(UUID.randomUUID(), 0);
        IssueCreatedForTesting domainEvent = new IssueCreatedForTesting();
        domainEvent.afterHeaderPropertiesSets(versionedAggregate);

        // Act
        boolean sut = domainEvent.isInitialized();

        // Assert
        Assert.assertTrue(sut);
    }

    @Test
    public void afterHeaderPropertiesSets_correctly() {
        // Arrange
        Random random = new Random();
        long version = random.nextInt(Integer.MAX_VALUE) + 1L;
        VersionedAggregate versionedAggregate =
                new VersionedIssueForTesting(UUID.randomUUID(), version);
        IssueCreatedForTesting domainEvent = new IssueCreatedForTesting();

        // Act
        domainEvent.afterHeaderPropertiesSets(versionedAggregate);

        // Assert
        Assert.assertNotNull(domainEvent.getAggregateId());
        Assert.assertEquals(domainEvent.getAggregateId(), versionedAggregate.getId());
        Assert.assertTrue(domainEvent.getVersion() > 0);
        Assert.assertTrue(domainEvent.getVersion() == versionedAggregate.getVersion() + 1);
        Assert.assertNotNull(domainEvent.getOccurrenceTime());
        ZonedDateTime after = ZonedDateTime.now();
        Assert.assertTrue(
                domainEvent.getOccurrenceTime().toEpochSecond()
                        <= after.toEpochSecond());
        Assert.assertTrue(
                domainEvent.getOccurrenceTime().toEpochSecond()
                        >= after.minusSeconds(1).toEpochSecond());
    }

    @Test
    public void afterHeaderPropertiesSets_for_already_initialized() {
        // Arrange
        VersionedAggregate versionedAggregate =
                new VersionedIssueForTesting(UUID.randomUUID(), 0);
        IssueCreatedForTesting domainEvent
                = new IssueCreatedForTesting(UUID.randomUUID(), 1, ZonedDateTime.now());

        // Act
        IllegalStateException expected = null;
        try {
            domainEvent.afterHeaderPropertiesSets(versionedAggregate);
        } catch (IllegalStateException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the state of the domain event.",
                expected.getMessage().contains("already initialized"));
    }

    @Test
    public void afterHeaderPropertiesSets_has_guard_clause_for_null_aggregateId() {
        // Arrange
        UUID aggregateId = null;
        VersionedAggregate versionedAggregate =
                new VersionedIssueForTesting(aggregateId, 0);
        IssueCreatedForTesting domainEvent = new IssueCreatedForTesting();

        // Act
        IllegalArgumentException expected = null;
        try {
            domainEvent.afterHeaderPropertiesSets(versionedAggregate);
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
    public void afterHeaderPropertiesSets_has_guard_clause_for_minimum_value_of_version() {
        // Arrange
        long version = -1;
        VersionedAggregate versionedAggregate =
                new VersionedIssueForTesting(UUID.randomUUID(), version);
        IssueCreatedForTesting domainEvent = new IssueCreatedForTesting();

        // Act
        IllegalArgumentException expected = null;
        try {
            domainEvent.afterHeaderPropertiesSets(versionedAggregate);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'version'.",
                expected.getMessage().contains("'version'"));
    }
}
