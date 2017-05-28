package io.loom.core.messaging;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonMessageSerializer implements MessageSerializer {
    private class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        TypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL);
        }

        @Override
        public boolean useForType(JavaType t) {
            return !t.isPrimitive();
        }
    }

    private final ObjectMapper mapper;

    {
        com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder<?> typer = new TypeResolverBuilder();
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = typer.typeProperty("$type");
        mapper = new ObjectMapper().setDefaultTyping(typer);
    }

    @Override
    public String serialize(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("The parameter 'message' cannot be null.");
        }

        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize message. See the cause for details.", e);
        }
    }

    @Override
    public Object deserialize(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The parameter 'value' cannot be null.");
        }

        try {
            return mapper.readValue(value, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not deserialize message. See the cause for details.", e);
        }
    }
}
