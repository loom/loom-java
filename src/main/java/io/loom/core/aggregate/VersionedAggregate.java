package io.loom.core.aggregate;

import java.util.UUID;

public interface VersionedAggregate {
    UUID getId();

    long getVersion();
}
