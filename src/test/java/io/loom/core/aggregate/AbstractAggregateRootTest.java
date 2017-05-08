package io.loom.core.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.loom.core.event.DomainEvent;
import io.loom.core.exception.EventApplyFailureException;
import io.loom.core.exception.EventHandlerNotFoundException;
import io.loom.core.fixtures.Issue;
import io.loom.core.fixtures.IssueEvent.IssueContentChanged;
import io.loom.core.fixtures.IssueEvent.IssueCreated;
import io.loom.core.fixtures.IssueEvent.IssueDeleted;
import io.loom.core.fixtures.IssueEvent.IssueTitleChanged;
import io.loom.core.fixtures.UnknownEvent;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by mhyeon.lee on 2017. 5. 7..
 */
public class AbstractAggregateRootTest {
    @Test
    public void default_new_instance_id_is_null_and_invalid_state_test() {
        // When
        ApplicableTestIssue issue = new ApplicableTestIssue();

        // Then
        assertNull(issue.getId());
        assertFalse(issue.isValid());
    }

    @Test
    public void pollAllPendingEvents_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.raise(issueTitleChanged);

        // When
        Iterable<DomainEvent> polledEvents = issue.pollAllPendingEvents();

        // Then
        Iterator<DomainEvent> polledIterator = polledEvents.iterator();
        assertEquals(IssueCreated.class, polledIterator.next().getClass());
        assertEquals(issueTitleChanged, polledIterator.next());
        assertFalse(polledIterator.hasNext());
    }

    @Test
    public void pollAllPendingEvents_empty_pending_events_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.raise(issueTitleChanged);
        issue.pollAllPendingEvents();

        // When
        Iterable<DomainEvent> remainedEvents = issue.pollAllPendingEvents();

        // Then
        assertFalse(remainedEvents.iterator().hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pollAllPendingEvents_unmodifiable_list_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        List<DomainEvent> polledEvents = (List<DomainEvent>) issue.pollAllPendingEvents();

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        polledEvents.add(issueTitleChanged);
    }

    @Test
    public void delete_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue1-title", "issue1-content");

        // When
        issue.delete();

        // Then
        assertTrue(issue.isDeleted());
    }

    @Test
    public void raise_event_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        issue.pollAllPendingEvents();

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.raise(issueTitleChanged);

        // Then
        assertEquals(issueTitleChanged.getAggregateId(), issue.getId());
        assertEquals(issueTitleChanged.getVersion(), issue.getVersion());
        Iterator<DomainEvent> events = issue.pollAllPendingEvents().iterator();
        assertEquals(issueTitleChanged, events.next());
        assertFalse(events.hasNext());
    }

    @Test
    public void apply_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);

        // Then
        assertEquals(issueTitleChanged.getVersion(), issue.getVersion());
        assertEquals(issueTitleChanged.getTitle(), issue.getTitle());
    }

    @Test
    public void apply_first_event_then_set_id_and_version_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue();

        // When
        IssueCreated issueCreated = new IssueCreated(
                UUID.randomUUID(), 1, "issue-title", "issue-content");
        issue.apply(issueCreated);

        // Then
        assertEquals(issueCreated.getAggregateId(), issue.getId());
        assertEquals(issueCreated.getVersion(), issue.getVersion());
        assertTrue(issue.isValid());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_event_aggregateId_is_null_throws_IllegalArgumentException_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                null, issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);
    }

    @Test(expected = IllegalStateException.class)
    public void apply_aggregate_already_deleted_throws_IllegalStateException_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");
        issue.delete();

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_event_aggregateId_is_not_equals_throws_IllegalArgumentException_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                UUID.randomUUID(), issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);
    }

    @Test(expected = IllegalStateException.class)
    public void apply_event_aggregate_invalid_state_IllegalStateException_test() {
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");
        try {
            IssueContentChanged issueContentChanged = new IssueContentChanged(
                    issue.getId(), issue.getVersion() + 1, "changed-content");
            issue.apply(issueContentChanged);
        } catch (EventApplyFailureException e) {
            // do nothing
        }

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_event_version_equals_IllegalArgumentException_test() {
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion(), "changed-title");
        issue.apply(issueTitleChanged);
    }

    @Test(expected = EventHandlerNotFoundException.class)
    public void apply_unknown_event_will_throws_EventHandlerNotFoundException_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        DomainEvent unknownEvent = new UnknownEvent(issue.getId(), issue.getVersion() + 1);
        issue.apply(unknownEvent);
    }

    @Test
    public void apply_EventHandlerNotFoundException_then_invalid_state_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");
        long currentVersion = issue.getVersion();

        // When
        try {
            DomainEvent unknownEvent = new UnknownEvent(issue.getId(), currentVersion + 1);
            issue.apply(unknownEvent);
        } catch (EventHandlerNotFoundException e) {
            // do nothing;
        }

        // Then
        assertFalse(issue.isValid());
    }

    @Test(expected = EventApplyFailureException.class)
    public void apply_runtimeException_throws_EventApplyFailureException_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueContentChanged issueContentChanged = new IssueContentChanged(
                issue.getId(), issue.getVersion() + 1, "changed-content");
        issue.apply(issueContentChanged);
    }

    @Test
    public void apply_EventApplyFailureException_then_invalid_state_test() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        try {
            IssueContentChanged issueContentChanged = new IssueContentChanged(
                    issue.getId(), issue.getVersion() + 1, "changed-content");
            issue.apply(issueContentChanged);
        } catch (EventApplyFailureException e) {
            // do nothing
        }

        // Then
        assertFalse(issue.isValid());
    }

    @Test
    public void apply_custom_event_handler_method_name_test() {
        // Given
        final String fakeTitle = "fake-title";
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content") {
            private void listener(IssueCreated event) {
                super.title = event.getTitle();
                super.content = event.getContent();
            }

            private void listener(IssueTitleChanged event) {
                super.title = fakeTitle;
            }

            @Override
            protected String getEventHandlerMethodName() {
                return "listener";
            }
        };

        // When
        IssueTitleChanged issueTitleChanged = new IssueTitleChanged(
                issue.getId(), issue.getVersion() + 1, "changed-title");
        issue.apply(issueTitleChanged);

        // Then
        assertEquals(fakeTitle, issue.getTitle());
    }

    // TODO: 상위 타입의 handler 를 찾을 수 있으면 이 테스트는 성공할 것입니다.
    @Test
    @Ignore
    public void apply_find_super_type_handler() {
        // Given
        ApplicableTestIssue issue = new ApplicableTestIssue(
                UUID.randomUUID(), "issue-title", "issue-content");

        // When
        IssueDeleted issueDeleted = new IssueDeleted(issue.getId(), issue.getVersion() + 1);
        issue.apply(issueDeleted);

        // Then
        assertTrue(issue.isDeleted());
    }

    @Test
    public void equals_and_hashCode_test() {
        // Given
        UUID issueId = UUID.randomUUID();
        Issue issue1 = new Issue(issueId, "issue1-title", "issue1-content");
        Issue issue2 = new Issue(issueId, "issue2-title", "issue2-content");

        // When
        boolean equals = issue1.equals(issue2);
        boolean hashCode = issue1.hashCode() == issue2.hashCode();

        // Then
        assertTrue(equals);
        assertTrue(hashCode);
    }

    @Test
    public void equals_difference_type_false_test() {
        // Given
        UUID issueId = UUID.randomUUID();
        Issue issue1 = new Issue(issueId, "issue-title", "issue-content");
        Issue issue2 = new ApplicableTestIssue(issueId, "issue-title", "issue-content");

        // When
        boolean equals = issue1.equals(issue2);

        // Then
        assertFalse(equals);
    }

    @Test
    public void hashCode_difference_type_false_test() {
        // Given
        UUID issueId = UUID.randomUUID();
        Issue issue1 = new Issue(issueId, "issue-title", "issue-content");
        Issue issue2 = new ApplicableTestIssue(issueId, "issue-title", "issue-content");

        // When
        boolean hashCode = issue1.hashCode() == issue2.hashCode();

        // Then
        assertTrue(hashCode);
    }

    @Test
    public void equals_and_hashCode_difference_id_false_test() {
        // Given
        Issue issue1 = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        Issue issue2 = new Issue(UUID.randomUUID(), "issue-title", "issue-content");

        // When
        boolean equals = issue1.equals(issue2);
        boolean hashCode = issue1.hashCode() == issue2.hashCode();

        // Then
        assertFalse(equals);
        assertFalse(hashCode);
    }

    @Test
    public void equals_and_hashCode_difference_version_false_test() {
        // Given
        Issue issue1 = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        Issue issue2 = new Issue(UUID.randomUUID(), "issue-title", "issue-content");
        IssueContentChanged issueContentChanged = new IssueContentChanged(
                issue2.getId(), issue2.getVersion() + 1, "issue-title");
        issue2.raise(issueContentChanged);

        // When
        boolean equals = issue1.equals(issue2);
        boolean hashCode = issue1.hashCode() == issue2.hashCode();

        // Then
        assertFalse(equals);
        assertFalse(hashCode);
    }

    @Test
    public void equals_and_hashCode_invalid_state_false_test()
            throws NoSuchFieldException, IllegalAccessException {
        // Given
        UUID issueId = UUID.randomUUID();
        Issue issue1 = new Issue(issueId, "issue-title", "issue-content");
        Issue issue2 = new Issue(issueId, "issue-title", "issue-content");
        Field validField = AbstractAggregateRoot.class.getDeclaredField("valid");
        validField.setAccessible(true);
        validField.set(issue2, false);

        // When
        boolean equals = issue1.equals(issue2);
        boolean hashCode = issue1.hashCode() == issue2.hashCode();

        // Then
        assertFalse(equals);
        assertFalse(hashCode);
    }

    @Test
    public void toString_test() {
        // Given
        Issue issue = new Issue(UUID.randomUUID(), "issue-title", "issue-content");

        // When
        String toString = issue.toString();

        // Then
        assertEquals(
                toString,
                "Issue"
                        + " {"
                        + "id=" + issue.getId()
                        + ", version=" + issue.getVersion()
                        + ", deleted=" + issue.isDeleted()
                        + '}');
    }

    static class ApplicableTestIssue extends Issue {
        // public no args constructor for testing
        public ApplicableTestIssue() {
        }

        public ApplicableTestIssue(UUID id, String title, String body) {
            super(id, title, body);
        }

        // override for testing
        @Override
        protected void apply(DomainEvent event) {
            super.apply(event);
        }

        @Override
        protected void handle(IssueCreated event) {
            super.handle(event);
        }

        @Override
        protected void handle(IssueTitleChanged event) {
            super.handle(event);
        }

        // throw exception for testing
        @Override
        protected void handle(IssueContentChanged event) {
            super.handle(event);
            throw new RuntimeException();
        }
    }
}
