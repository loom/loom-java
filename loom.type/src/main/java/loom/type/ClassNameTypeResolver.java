package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

final class ClassNameTypeResolver implements TypeResolver {

    public static final TypeResolver INSTANCE = new ClassNameTypeResolver();

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        try {
            return Optional.of(Class.forName(formattedType));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
