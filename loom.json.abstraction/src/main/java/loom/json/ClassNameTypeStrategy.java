package loom.json;

import java.lang.reflect.Type;
import java.util.Optional;

public final class ClassNameTypeStrategy implements TypeStrategy {

    @Override
    public String formatType(Object value) {
        return value.getClass().getName();
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
