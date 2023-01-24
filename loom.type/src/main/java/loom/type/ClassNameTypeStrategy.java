package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

@Deprecated
public final class ClassNameTypeStrategy implements TypeStrategy {

    @Override
    public Optional<String> tryFormatType(Type type) {
        return Optional.of(type.getTypeName());
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
