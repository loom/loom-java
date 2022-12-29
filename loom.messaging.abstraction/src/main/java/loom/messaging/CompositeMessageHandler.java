package loom.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CompositeMessageHandler implements MessageHandler {

    private final List<MessageHandler> handlers;

    public CompositeMessageHandler(MessageHandler... handlers) {
        this.handlers = Collections.unmodifiableList(Arrays.asList(handlers));
    }

    @Override
    public boolean canHandle(Message message) {
        for (MessageHandler handler : handlers) {
            if (handler.canHandle(message)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(Message message) {
        List<Throwable> errors = handleMessageCatchingErrors(message);

        if (errors.isEmpty() == false) {
            throw createException(errors);
        }
    }

    private List<Throwable> handleMessageCatchingErrors(Message message) {
        List<Throwable> errors = null;

        for (MessageHandler handler : handlers) {
            try {
                if (handler.canHandle(message)) {
                    handler.handle(message);
                }
            } catch (Throwable e) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(e);
            }
        }

        return errors == null ? Collections.emptyList() : errors;
    }

    private RuntimeException createException(List<Throwable> errors) {
        String message = aggregateMessages(errors);
        RuntimeException exception = new RuntimeException(message);
        errors.forEach(exception::addSuppressed);
        return exception;
    }

    private String aggregateMessages(List<Throwable> errors) {
        StringBuilder messageBuilder = new StringBuilder();
        errors.forEach(e -> messageBuilder
            .append(System.lineSeparator())
            .append("\tSuppressed: ")
            .append(e.getMessage()));
        return messageBuilder.toString();
    }
}
