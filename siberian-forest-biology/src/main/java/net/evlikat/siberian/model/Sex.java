package net.evlikat.siberian.model;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public class Sex {
    public static final Sex MALE = new Sex("male", Color.ORANGE);
    public static final Sex FEMALE = new Sex("female", Color.BLUE);

    private final String name;
    private final Color color;

    private Sex(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static Sex random() {
        return ThreadLocalRandom.current().nextInt(2) == 0 ? MALE : FEMALE;
    }

    @Override
    public String toString() {
        return name;
    }
}
