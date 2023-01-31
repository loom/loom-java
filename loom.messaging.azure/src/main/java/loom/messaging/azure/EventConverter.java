package loom.messaging.azure;

import com.azure.messaging.eventhubs.EventData;
import loom.json.JsonStrategy;
import loom.messaging.Message;
import loom.type.TypeStrategy;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class EventConverter {
    private final JsonStrategy _jsonStrategy;
    private final TypeStrategy _typeStrategy;

    public EventConverter(
        JsonStrategy jsonStrategy,
        TypeStrategy typeStrategy
    ) {
        _jsonStrategy = jsonStrategy;
        _typeStrategy = typeStrategy;
    }

    public EventData convertToEvent(Message message) {
        String body = _jsonStrategy.serialize(message.getData());
        EventData eventData = new EventData(body);
        setProperties(message, eventData);
        return eventData;
    }

    public Optional<Message> tryRestoreMessage(EventData eventData) {
        return eventData.getBody() == null
            ? Optional.empty()
            : _typeStrategy
                .tryResolveType((String)eventData.getProperties().get("Type"))
                .flatMap(t -> Optional.of(getMessage(eventData, t)));
    }

    private Message getMessage(EventData eventData, Type t) {
        return new Message(
            "",
            (String) eventData.getProperties().get("ProcessId"),
            "",
            (String) eventData.getProperties().get("PredecessorId"),
            _jsonStrategy.deserialize(t, eventData.getBodyAsString()));
    }

    private void setProperties(Message message, EventData eventData) {
        String type = _typeStrategy.formatTypeOf(message.getData());
        Map<String, Object> properties = eventData.getProperties();
        properties.put("Type", type);
        properties.put("ProcessId", message.getProcessId());
        properties.put("PredecessorId", message.getPredecessorId());
    }
}
