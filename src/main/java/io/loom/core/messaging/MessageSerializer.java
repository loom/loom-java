package io.loom.core.messaging;

public interface MessageSerializer {
    String serialize(Object message);

    Object deserialize(String value);
}
