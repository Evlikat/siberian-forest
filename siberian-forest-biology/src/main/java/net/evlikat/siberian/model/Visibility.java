package net.evlikat.siberian.model;

import java.util.stream.Stream;

public interface Visibility extends Sized {

    int getWidth();

    int getHeight();

    Stream<LivingUnit> units();

    Stream<Cell> cells();

    Visibility local(Position p);
}
