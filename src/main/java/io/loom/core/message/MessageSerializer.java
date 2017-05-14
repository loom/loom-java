package io.loom.core.message;

public interface MessageSerializer {
    String serialize(Object message);

    Object deserialize(String value);
}
