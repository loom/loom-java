package loom.messaging;

public interface MessageHandler {

    boolean canHandle(Message message);

    void handle(Message message);
}
