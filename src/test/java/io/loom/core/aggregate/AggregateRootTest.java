package io.loom.core.aggregate;

import static org.junit.Assert.assertEquals;

import io.loom.core.fixtures.Issue;
import java.util.UUID;
import org.junit.Test;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class AggregateRootTest {
  @Test
  public void changeTitle_increase_version() {
    // Given
    Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");

    // When
    issue.changeTitle("changed-title");

    // Then
    assertEquals(issue.getVersion(), 1L);
  }
}
