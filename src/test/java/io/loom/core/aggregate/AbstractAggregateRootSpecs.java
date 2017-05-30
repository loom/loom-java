package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;
import io.loom.core.event.LazyInitializedDomainEvent;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class AbstractAggregateRootSpecs {
    public class IssueForTesting extends AbstractAggregateRoot {
        public IssueForTesting(UUID id) {
            super(id);
        }
    }

    public class IssueEventForTesting implements DomainEvent {
        private UUID aggregateId;
        private long version;
        private ZonedDateTime occurrenceTime;

        /**
         * Instantiates a new Issue event for testing.
         *
         * @param aggregateId    the aggregate id
         * @param version        the version
         * @param occurrenceTime the occurrence time
         */
        public IssueEventForTesting(UUID aggregateId, long version, ZonedDateTime occurrenceTime) {
            this.aggregateId = aggregateId;
            this.version = version;
            this.occurrenceTime = occurrenceTime;
        }

        @Override
        public UUID getAggregateId() {
            return this.aggregateId;
        }

        @Override
        public long getVersion() {
            return this.version;
        }

        @Override
        public ZonedDateTime getOccurrenceTime() {
            return this.occurrenceTime;
        }
    }

    public class LazyEventForTesting implements LazyInitializedDomainEvent {
        public boolean initialized;
        private ZonedDateTime occurrenceTime;

        public LazyEventForTesting() {
        }

        @Override
        public boolean isInitialized() {
            return this.initialized;
        }

        @Override
        public void afterHeaderPropertiesSets(VersionedAggregate versionedAggregate) {
            this.initialized = true;
            this.occurrenceTime = ZonedDateTime.now();
        }

        @Override
        public UUID getAggregateId() {
            return null;
        }

        @Override
        public long getVersion() {
            return 0;
        }

        @Override
        public ZonedDateTime getOccurrenceTime() {
            return occurrenceTime;
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
    public void raise_has_guard_clause_for_null_domainEvent() {
        // Arrange
        IssueForTesting issue = new IssueForTesting(UUID.randomUUID());

        // Act
        IllegalArgumentException expected = null;
        try {
            issue.raise(null);
        } catch (IllegalArgumentException e) {
            expected = e;
        }

        // Assert
        Assert.assertNotNull(expected);
        Assert.assertTrue(
                "The error message should contain the name of the parameter 'domainEvent'.",
                expected.getMessage().contains("'domainEvent'"));
    }

    @Test
    public void raise_lazyEvent_afterHeaderPropertiesSets_correctly() {
        // Arrange
        IssueForTesting issue = new IssueForTesting(UUID.randomUUID());
        LazyEventForTesting unInitializedLazyEvent = new LazyEventForTesting();

        // Act
        issue.raise(unInitializedLazyEvent);

        // Assert
        Assert.assertTrue(unInitializedLazyEvent.isInitialized());
        Assert.assertNotNull(unInitializedLazyEvent.getOccurrenceTime());
    }

    @Test
    public void raise_initialized_lazyEvent_do_not_invoke_afterHeaderPropertiesSets() {
        // Arrange
        IssueForTesting issue = new IssueForTesting(UUID.randomUUID());
        LazyEventForTesting initializedLazyEvent = new LazyEventForTesting();
        initializedLazyEvent.initialized = true;


        // Act
        issue.raise(initializedLazyEvent);

        // Assert
        Assert.assertTrue(initializedLazyEvent.isInitialized());
        Assert.assertNull(initializedLazyEvent.getOccurrenceTime());
    }

    // LazyInitializedDomainEvent 를 구현하지 않은 event 에는 변경이 발생하지 않는 테스트 입니다.
    // raise() 에서 domainEvent 의 header properties sets 를 검사하는 guard 로직이 추가되면
    // 이 테스트는 실패하고 테스트를 조건을 수정해야 합니다.
    @Test
    public void raise_pure_domain_event_do_not_change_header_properties_set() {
        // Arrange
        IssueForTesting issue = new IssueForTesting(UUID.randomUUID());

        UUID aggregateId = UUID.randomUUID();
        Random random = new Random();
        long version = random.nextInt(Integer.MAX_VALUE) + 1L;
        ZonedDateTime occurrenceTime = ZonedDateTime.now().plusNanos(random.nextInt());
        IssueEventForTesting pureDomainEvent =
                new IssueEventForTesting(aggregateId, version, occurrenceTime);

        // Act
        issue.raise(pureDomainEvent);

        // Assert
        Assert.assertEquals(aggregateId, pureDomainEvent.getAggregateId());
        Assert.assertEquals(version, pureDomainEvent.getVersion());
        Assert.assertEquals(occurrenceTime, pureDomainEvent.getOccurrenceTime());
    }
}
