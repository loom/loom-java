package loom.eventsourcing;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;
import loom.type.TypeStrategy;

public class StreamEventTypeStrategy implements TypeStrategy {

    private final Supplier<TypeStrategy> payloadStrategyFactory;
    private final String prefix;

    public StreamEventTypeStrategy(
        Supplier<TypeStrategy> payloadStrategyFactory,
        String prefix
    ) {
        this.payloadStrategyFactory = payloadStrategyFactory;
        this.prefix = prefix;
    }

    public StreamEventTypeStrategy(
        Supplier<TypeStrategy> payloadStrategyFactory
    ) {
        this(payloadStrategyFactory, "streamevent");
    }

    @Override
    public Optional<String> tryFormatType(Type type) {
        return Optional.empty();
    }

    @Override
    public Optional<String> tryFormatTypeOf(Object value) {
        return value instanceof StreamEvent
            ? Optional.of(formatStreamCommandTypeOf(value))
            : Optional.empty();
    }

    private String formatStreamCommandTypeOf(Object value) {
        StreamEvent<?> event = (StreamEvent<?>) value;
        Object payload = event.getPayload();
        TypeStrategy payloadStrategy = payloadStrategyFactory.get();
        return prefix + ":" + payloadStrategy.formatTypeOf(payload);
    }

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        String[] parts = formattedType.split(":", 2);
        if (parts.length == 2) {
            String prefix = parts[0];
            if (prefix.equals(this.prefix)) {
                TypeStrategy payloadStrategy = payloadStrategyFactory.get();
                String formattedPayloadType = parts[1];
                Optional<Type> payloadType =
                    payloadStrategy.tryResolveType(formattedPayloadType);
                return payloadType.map(StreamEventType::new);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private static final class StreamEventType extends GenericType {

        public StreamEventType(Type payloadType) {
            super(StreamEvent.class, payloadType);
        }
    }
}
