package io.loom;

/**
 * Created by mhyeon.lee on 2017. 5. 3..
 */
public class Loom {
    private final String name;

    public Loom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void main(String... args) {
        Loom loom = new Loom("loom-core");
        System.out.println("Hello " + loom.getName());
    }
}
