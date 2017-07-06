package io.loom.core.messaging;

public interface MessageSerializer {
    String serialize(Message message);

    Message deserialize(String value);
}
