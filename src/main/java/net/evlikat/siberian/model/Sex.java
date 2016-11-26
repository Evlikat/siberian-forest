package net.evlikat.siberian.model;

import java.util.concurrent.ThreadLocalRandom;

public enum Sex {
    MALE,
    FEMALE;

    public static Sex random() {
        return values()[ThreadLocalRandom.current().nextInt(2)];
    }
}
