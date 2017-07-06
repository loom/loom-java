package io.loom.core.messaging;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDefaultTyping(typer);
    }

    @Override
    public String serialize(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("The parameter 'message' cannot be null.");
        }

        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            String errorMessage = "Could not serialize message. See the cause for details.";
            throw new RuntimeException(errorMessage, e);
        }
    }

    @Override
    public Message deserialize(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The parameter 'value' cannot be null.");
        }

        try {
            return (Message)mapper.readValue(value, Object.class);
        } catch (IOException e) {
            String errorMessage = "Could not deserialize message. See the cause for details.";
            throw new RuntimeException(errorMessage, e);
        }
    }
}
