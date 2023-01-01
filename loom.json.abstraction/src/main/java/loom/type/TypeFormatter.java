package loom.type;

import java.util.Optional;

@FunctionalInterface
public interface TypeFormatter {

    Optional<String> tryFormatType(Object value);

    default String formatType(Object value) {
        return tryFormatType(value).orElseThrow(() -> {
            String message = "Unable to format type: " + value.getClass().getName();
            return new RuntimeException(message);
        });
    }
}
