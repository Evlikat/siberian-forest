package net.evlikat.siberian.model;

import java.util.concurrent.ThreadLocalRandom;

public class Sex {

    public static final Sex MALE = new Sex("male");
    public static final Sex FEMALE = new Sex("female");

    private final String name;

    private Sex(String name) {
        this.name = name;
    }

    public static Sex random() {
        return ThreadLocalRandom.current().nextInt(2) == 0 ? MALE : FEMALE;
    }

    @Override
    public String toString() {
        return name;
    }
}
