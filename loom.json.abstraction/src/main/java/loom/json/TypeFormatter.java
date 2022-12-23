package loom.json;

@FunctionalInterface
public interface TypeFormatter {

    String formatType(Object value);
}
