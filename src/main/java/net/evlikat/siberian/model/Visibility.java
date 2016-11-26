package net.evlikat.siberian.model;

import java.util.stream.Stream;

public interface Visibility {

    int getWidth();

    int getHeight();

    Stream<LivingUnit> units();
}
