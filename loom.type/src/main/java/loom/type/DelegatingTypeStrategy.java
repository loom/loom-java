package loom.type;

import java.lang.reflect.Type;
import java.util.Optional;

final class DelegatingTypeStrategy implements TypeStrategy {

    private final TypeFormatter formatter;
    private final TypeResolver resolver;

    public DelegatingTypeStrategy(
        TypeFormatter formatter,
        TypeResolver resolver
    ) {
        this.formatter = formatter;
        this.resolver = resolver;
    }

    @Override
    public Optional<String> tryFormatType(Type type) {
        return formatter.tryFormatType(type);
    }

    @Override
    public Optional<Type> tryResolveType(String formattedType) {
        return resolver.tryResolveType(formattedType);
    }
}
