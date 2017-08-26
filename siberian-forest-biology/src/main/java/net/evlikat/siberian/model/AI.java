package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Optional;

public interface AI<T extends LivingUnitInfo> {

    Optional<Position> move(T unit, Visibility visibility);

    Optional<Food> feed(T unit, Visibility visibility);
}
