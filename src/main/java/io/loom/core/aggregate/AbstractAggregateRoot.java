package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;
import io.loom.core.exception.EventApplyFailureException;
import io.loom.core.exception.EventHandlerNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 7..
 */
public abstract class AbstractAggregateRoot implements AggregateRoot {
    private static final String DEFAULT_EVENT_HANDLER_METHOD_NAME = "handle";

    private final transient List<DomainEvent> pendingEvents = new ArrayList<>();
    private UUID id;
    private long version;
    private boolean deleted;
    private boolean valid;

    protected AbstractAggregateRoot() {
    }

    @Override
    public Iterable<DomainEvent> pollAllPendingEvents() {
        Iterable<DomainEvent> pollEvents = Collections.emptyList();
        if (!this.pendingEvents.isEmpty()) {
            pollEvents = Collections.unmodifiableList(new ArrayList<>(this.pendingEvents));
            this.pendingEvents.clear();
        }
        return pollEvents;
    }

    protected void raise(DomainEvent event) {
        apply(event);
        this.pendingEvents.add(event);
    }

    // TODO: Reflection 은 비용이 비싸므로 handler method 는 cache 해야 합니다.
    protected void apply(DomainEvent event) {
        validateApplyEvent(event);
        if (this.getId() == null) {
            this.id = event.getAggregateId();
            this.valid = true;
        }
        this.version = event.getVersion();

        try {
            // TODO: 부모 클래스에 protected 이상 레벨의 handler 가 있으면 찾을 수 있어야 한다.
            Method method = this.getClass().getDeclaredMethod(
                    getEventHandlerMethodName(), event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (NoSuchMethodException e) {
            this.valid = false;
            throw new EventHandlerNotFoundException(getClass(), event.getClass(), e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            this.valid = false;
            throw new EventApplyFailureException(e.getMessage(), e);
        }
    }

    private void validateApplyEvent(DomainEvent event) {
        if (event.getAggregateId() == null) {
            throw new IllegalArgumentException("aggregateId of event must not be null.");
        }
        if (this.isDeleted()) {
            throw new IllegalStateException("Aggregate is already deleted.");
        }
        UUID aggregateId = this.getId();
        if (aggregateId != null) {
            if (!aggregateId.equals(event.getAggregateId())) {
                throw new IllegalArgumentException("Applied aggregateId is not equal.");
            }
            if (!this.isValid()) {
                throw new IllegalStateException("Aggregate state is invalid.");
            }
        }
        if (event.getVersion() <= this.getVersion()) {
            throw new IllegalArgumentException(
                    "The version must be greater than the current version.");
        }
    }

    protected final void delete() {
        this.deleted = true;
    }

    protected String getEventHandlerMethodName() {
        return DEFAULT_EVENT_HANDLER_METHOD_NAME;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    protected boolean isDeleted() {
        return this.deleted;
    }

    protected boolean isValid() {
        return this.valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        if (o instanceof AbstractAggregateRoot
                && this.isValid() != AbstractAggregateRoot.class.cast(o).isValid()) {
            return false;
        }

        AggregateRoot other = (AggregateRoot) o;
        if (!this.getId().equals(other.getId())) {
            return false;
        }

        return this.getVersion() == other.getVersion();
    }

    @Override
    public int hashCode() {
        int result = this.getId().hashCode();
        result = 31 * result + (int) (this.getVersion() ^ (this.getVersion() >>> 32));
        result = 31 * result + (this.isValid() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String stringValue = new StringJoiner(", ")
                .add("id=" + this.getId())
                .add("version=" + this.getVersion())
                .add("deleted=" + this.isDeleted())
                .toString();
        return this.getClass().getSimpleName() + " {" + stringValue + '}';
    }
}
