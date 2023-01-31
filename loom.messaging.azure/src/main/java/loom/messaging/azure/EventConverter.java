package loom.messaging.azure;

import com.azure.messaging.eventhubs.EventData;
import loom.json.JsonStrategy;
import loom.messaging.Message;
import loom.type.TypeStrategy;

import java.util.Map;
import java.util.Properties;

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

    private void setProperties(Message message, EventData eventData) {
        String type = _typeStrategy.formatTypeOf(message.getData());
        Map<String, Object> properties = eventData.getProperties();
        properties.put("Type", type);
        properties.put("ProcessId", message.getProcessId());
        properties.put("PredecessorId", message.getPredecessorId());
    }
}
