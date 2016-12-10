package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.geo.Sized;

import java.util.stream.Stream;

public interface Visibility extends Sized {

    int getWidth();

    int getHeight();

    Stream<LivingUnit> units();

    Stream<Cell> cells();

    Visibility local(Position p);
}
