package loom.json;

import java.lang.reflect.Type;
import java.util.Optional;

public class DelegatingTypeStrategy implements TypeStrategy {

    private final TypeFormatter typeFormatter;
    private final TypeResolver typeResolver;

    public DelegatingTypeStrategy(
        TypeFormatter typeFormatter,
        TypeResolver typeResolver
    ) {
        this.typeFormatter = typeFormatter;
        this.typeResolver = typeResolver;
    }

    @Override
    public String formatType(Object value) {
        return typeFormatter.formatType(value);
    }

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        return typeResolver.tryResolveType(formattedType);
    }
}
