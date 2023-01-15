package loom.messaging;

import static java.util.Arrays.asList;

public interface MessageBus {

    void send(String partitionKey, Iterable<Message> messages);

    default void send(String partitionKey, Message message) {
        send(partitionKey, asList(message));
    }
}
