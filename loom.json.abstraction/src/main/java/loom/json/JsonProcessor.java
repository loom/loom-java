package loom.json;

import java.lang.reflect.Type;
import loom.type.TypeStrategy;

public final class JsonProcessor {

    private final TypeStrategy typeStrategy;
    private final JsonStrategy jsonStrategy;

    public JsonProcessor(TypeStrategy typeStrategy, JsonStrategy jsonStrategy) {
        this.typeStrategy = typeStrategy;
        this.jsonStrategy = jsonStrategy;
    }

    public JsonData convertToJson(Object value) {
        String formattedType = typeStrategy.formatType(value);
        String json = jsonStrategy.serialize(value);
        return new JsonData(formattedType, json);
    }

    public Object convertFromJson(JsonData jsonData) {
        String formattedType = jsonData.getType();
        String json = jsonData.getContent();
        Type type = typeStrategy.resolveType(formattedType);
        return jsonStrategy.deserialize(type, json);
    }
}
