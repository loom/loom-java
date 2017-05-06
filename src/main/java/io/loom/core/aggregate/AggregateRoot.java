package io.loom.core.aggregate;

import io.loom.core.event.DomainEvent;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public interface AggregateRoot extends Serializable {
    UUID getId();

    long getVersion();

    Iterable<DomainEvent> pollEvents();
}
