package loom.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import loom.eventsourcing.StreamCommand;
import loom.eventsourcing.StreamEvent;

@Deprecated
public final class EventSourcingTypeStrategy implements TypeStrategy {

    private final TypeStrategy payloadStrategy;

    public EventSourcingTypeStrategy(TypeStrategy payloadStrategy) {
        this.payloadStrategy = payloadStrategy;
    }

    @Override
    public Optional<String> tryFormatType(Type type) {
        return Optional.empty();
    }

    @Override
    public Optional<String> tryFormatType(Object value) {
        if (value instanceof StreamCommand) {
            StreamCommand<?> command = (StreamCommand<?>) value;
            Object payload = command.getPayload();
            String payloadType = payloadStrategy.formatTypeOf(payload);
            return Optional.of("stream-command:" + payloadType);
        } else if (value instanceof StreamEvent) {
            StreamEvent<?> event = (StreamEvent<?>) value;
            Object payload = event.getPayload();
            String payloadType = payloadStrategy.formatTypeOf(payload);
            return Optional.of("stream-event:" + payloadType);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        String[] parts = formattedType.split(":", 2);
        if (parts.length == 2) {
            String prefix = parts[0];
            String formattedPayloadType = parts[1];
            Optional<Type> payloadType =
                payloadStrategy.tryResolveType(formattedPayloadType);
            if (prefix.equals("stream-command")) {
                return payloadType.map(StreamCommandType::new);
            } else if (prefix.equals("stream-event")) {
                return payloadType.map(StreamEventType::new);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private static final class StreamCommandType implements ParameterizedType {
        private final Type payloadType;

        public StreamCommandType(Type payloadType) {
            this.payloadType = payloadType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] { payloadType };
        }

        @Override
        public Type getRawType() {
            return StreamCommand.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    private static final class StreamEventType implements ParameterizedType {
        private final Type payloadType;

        public StreamEventType(Type payloadType) {
            this.payloadType = payloadType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] { payloadType };
        }

        @Override
        public Type getRawType() {
            return StreamEvent.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
