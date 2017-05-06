package io.loom.core.fixtures;

import io.loom.core.aggregate.AggregateRoot;
import io.loom.core.event.DomainEvent;
import io.loom.core.fixtures.IssueEvent.IssueContentChanged;
import io.loom.core.fixtures.IssueEvent.IssueCreated;
import io.loom.core.fixtures.IssueEvent.IssueTitleChanged;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class Issue implements AggregateRoot {
  private final List<IssueEvent> events = new ArrayList<>();

  private UUID id;
  private long version;
  private String title;
  private String content;

  Issue() {
  }

  public Issue(UUID id, String title, String body) {
    raise(new IssueCreated(id, 0, title, body));
  }

  public void changeTitle(String title) {
    raise(new IssueTitleChanged(getId(), version + 1, title));
  }

  public void changeContent(String content) {
    raise(new IssueContentChanged(getId(), version + 1, content));
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public long getVersion() {
    return version;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  private void raise(IssueEvent event) {
    apply(event);
    events.add(event);
  }

  /**
   * 이 메소드만 특이하게 javadoc을 작성해야한다는 checkstyle 경고가 있습니다.
   * 이유를 모르겠습니다
   * */
  public void apply(DomainEvent event) {
    if (event instanceof IssueCreated) {
      applyIssueCreated(IssueCreated.class.cast(event));
    } else if (event instanceof IssueTitleChanged) {
      applyIssueTitleChanged(IssueTitleChanged.class.cast(event));
    } else if (event instanceof IssueContentChanged) {
      applyIssueContentChanged(IssueContentChanged.class.cast(event));
    } else {
      throw new IllegalArgumentException(event.getClass() + " is not supported.");
    }
  }

  private void applyIssueCreated(IssueCreated event) {
    this.id = event.getAggregateId();
    this.title = event.getTitle();
    this.content = event.getContent();
    this.version = event.getVersion();
  }

  private void applyIssueTitleChanged(IssueTitleChanged event) {
    this.title = event.getTitle();
    this.version = event.getVersion();
  }

  private void applyIssueContentChanged(IssueContentChanged event) {
    this.content = event.getContent();
    this.version = event.getVersion();
  }

  @Override
  public Iterable<DomainEvent> pollEvents() {
    List<IssueEvent> events = new ArrayList<>(this.events);
    this.events.clear();
    return Collections.unmodifiableList(events);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Issue issue = (Issue) o;
    if (version != issue.version) {
      return false;
    }
    return id.equals(issue.id);

  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + (int) (version ^ (version >>> 32));
    return result;
  }
}
