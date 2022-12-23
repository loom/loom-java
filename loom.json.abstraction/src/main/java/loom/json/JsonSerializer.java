package loom.json;

@FunctionalInterface
public interface JsonSerializer {

    String serialize(Object value);
}
