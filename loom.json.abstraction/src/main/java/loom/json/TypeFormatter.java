package loom.json;

import java.util.Optional;

@FunctionalInterface
public interface TypeFormatter {

    Optional<String> tryFormatType(Object value);
}
