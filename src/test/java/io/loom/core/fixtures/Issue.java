package io.loom.core.fixtures;

import io.loom.core.aggregate.AbstractAggregateRoot;
import io.loom.core.event.DomainEvent;
import io.loom.core.fixtures.IssueEvent.IssueContentChanged;
import io.loom.core.fixtures.IssueEvent.IssueCreated;
import io.loom.core.fixtures.IssueEvent.IssueDeleted;
import io.loom.core.fixtures.IssueEvent.IssueTitleChanged;

import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class Issue extends AbstractAggregateRoot {
    protected String title;
    protected String content;

    protected Issue() {
    }

    public Issue(UUID id, String title, String body) {
        raise(new IssueCreated(id, getVersion() + 1, title, body));
    }

    public void changeTitle(String title) {
        raise(new IssueTitleChanged(getId(), getVersion() + 1, title));
    }

    public void changeContent(String content) {
        raise(new IssueContentChanged(getId(), getVersion() + 1, content));
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    protected void apply(DomainEvent event) {
        super.apply(event);
    }

    protected void handle(IssueCreated event) {
        this.title = event.getTitle();
        this.content = event.getContent();
    }

    protected void handle(IssueTitleChanged event) {
        this.title = event.getTitle();
    }

    protected void handle(IssueContentChanged event) {
        this.content = event.getContent();
    }

    protected void handle(IssueDeleted event) {
        super.delete();
    }
}
