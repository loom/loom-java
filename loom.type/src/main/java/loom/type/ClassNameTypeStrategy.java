package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

public final class ClassNameTypeStrategy implements TypeStrategy {

    @Override
    public Optional<String> tryFormatType(Object value) {
        return Optional.of(value.getClass().getName());
    }

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        try {
            return Optional.of(Class.forName(formattedType));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
