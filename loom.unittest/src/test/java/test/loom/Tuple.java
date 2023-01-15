package test.loom;

import lombok.Value;

@Value
public class Tuple<T1, T2> {
    private final T1 item1;
    private final T2 item2;
}
