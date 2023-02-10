package loom.messaging.azure;

import com.azure.messaging.eventhubs.EventData;
import loom.messaging.Message;
import loom.messaging.MessageHandler;

import java.util.Optional;

public class EventProcessor {

    private final EventConverter _eventConverter;
    private final MessageHandler _messageHandler;

    public EventProcessor(EventConverter eventConverter, MessageHandler messageHandler) {

        _eventConverter = eventConverter;
        _messageHandler = messageHandler;
    }

    public void process(EventData eventData) {
        Optional<Message> message = _eventConverter.tryRestoreMessage(eventData);
        message.ifPresent(_messageHandler::handle);
    }
}
