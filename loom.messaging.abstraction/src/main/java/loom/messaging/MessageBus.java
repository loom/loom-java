package loom.messaging;

public interface MessageBus {

    void send(String partitionKey, Iterable<Message> messages);
}
