package io.loom.core.entity;

import java.util.UUID;

public interface VersionedEntity {
    UUID getId();

    long getVersion();
}
