package loom.json;

import java.lang.reflect.Type;

@FunctionalInterface
public interface JsonDeserializer {

    Object deserialize(Type type, String json);
}
