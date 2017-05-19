package io.loom.core.aggregate;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

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
}
