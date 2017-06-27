package io.loom.core.messaging;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.ZonedDateTime;

public class JacksonMessageSerializer implements MessageSerializer {
    private class InternalTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        InternalTypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL);
        }

        @Override
        public boolean useForType(JavaType t) {
            return !t.isPrimitive() && t.getRawClass() != ZonedDateTime.class;
        }
    }

    private final ObjectMapper mapper;

    {
        TypeResolverBuilder<?> typer = new InternalTypeResolverBuilder();
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = typer.typeProperty("$type");
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setDefaultTyping(typer);
    }

    @Override
    public String serialize(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("The parameter 'message' cannot be null.");
        }

        Class<?> type = message.getClass();
        if (type.equals(Boolean.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Boolean.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Byte.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Byte.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Character.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Character.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Float.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Float.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Integer.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Integer.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Long.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Long.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Short.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Short.";
            throw new IllegalArgumentException(errorMessage);
        } else if (type.equals(Double.class)) {
            String errorMessage = "The parameter 'message' cannot be of java.lang.Double.";
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            String errorMessage = "Could not serialize message. See the cause for details.";
            throw new RuntimeException(errorMessage, e);
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
            String errorMessage = "Could not deserialize message. See the cause for details.";
            throw new RuntimeException(errorMessage, e);
        }
    }
}
