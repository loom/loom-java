package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

@FunctionalInterface
public interface TypeResolver {

    Optional<Type> tryResolveType(String formattedType);

    default Type resolveType(String formattedType) {
        return tryResolveType(formattedType).orElseThrow(() -> {
            String message = "Unable to resolve type for: " + formattedType;
            return new RuntimeException(message);
        });
    }
}
