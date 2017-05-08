package io.loom.core.event;

import io.loom.core.aggregate.AggregateRoot;

public interface EventElement<AggregateT extends AggregateRoot> {
    AggregateT applyTo(AggregateT aggregate);
}
