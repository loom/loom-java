package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

final class TypeNameFormatter implements TypeFormatter {

    public static final TypeFormatter INSTANCE = new TypeNameFormatter();

    @Override
    public Optional<String> tryFormatType(Type type) {
        return Optional.of(type.getTypeName());
    }
}
