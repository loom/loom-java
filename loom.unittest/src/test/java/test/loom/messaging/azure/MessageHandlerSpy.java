package test.loom.messaging.azure;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import loom.messaging.Message;
import loom.messaging.MessageHandler;

public class MessageHandlerSpy implements MessageHandler {
    private Queue<Message> messages = new ConcurrentLinkedQueue<>();
    private final Queue<Message> logs = messages;

    @Override
    public boolean canHandle(Message message) {
        return true;
    }

    @Override
    public void handle(Message message) {
        messages.add(message);
    }

    public Queue<Message> getLogs() {
        return logs;
    }
}
