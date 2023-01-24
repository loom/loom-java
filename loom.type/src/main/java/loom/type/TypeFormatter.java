package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

@FunctionalInterface
public interface TypeFormatter {

    Optional<String> tryFormatType(Type type);

    @Deprecated
    default Optional<String> tryFormatType(Object value) {
        return tryFormatTypeOf(value);
    }

    default Optional<String> tryFormatTypeOf(Object value) {
        return tryFormatType(value.getClass());
    }

    default String formatType(Type type) {
        return tryFormatType(type).orElseThrow(() -> {
            String message = "Unable to format type: " + type.getTypeName();
            return new RuntimeException(message);
        });
    }

    @Deprecated
    default String formatType(Object value) {
        return tryFormatType(value).orElseThrow(() -> {
            String message = "Unable to format type: " + value.getClass().getName();
            return new RuntimeException(message);
        });
    }

    default String formatTypeOf(Object value) {
        return tryFormatTypeOf(value).orElseThrow(() -> {
            Type type = value.getClass();
            String message = "Unable to format type: " + type.getTypeName();
            return new RuntimeException(message);
        });
    }

    static TypeFormatter forTypeName() {
        return TypeNameFormatter.INSTANCE;
    }
}
