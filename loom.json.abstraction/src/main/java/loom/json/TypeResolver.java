package loom.json;

import java.lang.reflect.Type;
import java.util.Optional;

@FunctionalInterface
public interface TypeResolver {

    Optional<Type> tryResolveType(String formattedType);
}
