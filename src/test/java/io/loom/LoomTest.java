package io.loom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class LoomTest {
    @Test
    public void loomTest() {
        // Given
        String name = "loom-core-test";
        Loom loom = new Loom(name);

        // When
        String loomName = loom.getName();

        // Then
        assertEquals(loomName, name);
    }
}
