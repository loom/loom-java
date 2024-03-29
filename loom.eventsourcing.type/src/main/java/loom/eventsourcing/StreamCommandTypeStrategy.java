package loom.eventsourcing;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;
import loom.type.TypeStrategy;

public class StreamCommandTypeStrategy implements TypeStrategy {

    private final Supplier<TypeStrategy> payloadStrategyFactory;
    private final String prefix;

    public StreamCommandTypeStrategy(
        Supplier<TypeStrategy> payloadStrategyFactory,
        String prefix
    ) {
        this.payloadStrategyFactory = payloadStrategyFactory;
        this.prefix = prefix;
    }

    public StreamCommandTypeStrategy(
        Supplier<TypeStrategy> payloadStrategyFactory
    ) {
        this(payloadStrategyFactory, "streamcommand");
    }

    @Override
    public Optional<String> tryFormatType(Type type) {
        return Optional.empty();
    }

    @Override
    public Optional<String> tryFormatTypeOf(Object value) {
        return value instanceof StreamCommand
            ? Optional.of(formatStreamCommandTypeOf(value))
            : Optional.empty();
    }

    private String formatStreamCommandTypeOf(Object value) {
        StreamCommand<?> command = (StreamCommand<?>) value;
        Object payload = command.getPayload();
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
                return payloadType.map(StreamCommandType::new);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private static final class StreamCommandType extends GenericType {

        public StreamCommandType(Type payloadType) {
            super(StreamCommand.class, payloadType);
        }
    }
}
