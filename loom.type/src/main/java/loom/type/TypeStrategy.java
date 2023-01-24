package loom.type;

public interface TypeStrategy extends TypeFormatter, TypeResolver {

    static TypeStrategy create(
        TypeFormatter formatter,
        TypeResolver resolver
    ) {
        return new DelegatingTypeStrategy(formatter, resolver);
    }
}
