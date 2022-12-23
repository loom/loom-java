package loom.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

public class JacksonJsonStrategy implements JsonStrategy {

    private final ObjectMapper mapper;

    public JacksonJsonStrategy(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(Type type, String json) {
        try {
            TypeFactory typeFactory = TypeFactory.defaultInstance();
            JavaType javaType = typeFactory.constructType(type);
            return mapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
