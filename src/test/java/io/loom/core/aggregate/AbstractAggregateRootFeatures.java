package io.loom.core.aggregate;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class AbstractAggregateRootFeatures {
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
                expected.getMessage().contains("id"));
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
}
