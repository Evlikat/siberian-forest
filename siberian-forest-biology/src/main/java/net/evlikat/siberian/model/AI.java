package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Map;
import java.util.Optional;

public interface AI<T extends LivingUnitInfo> {

    Map<Position, Integer> evaluate(T unit, Visibility visibility);

    Optional<Position> move(T unit, Visibility visibility);

    Optional<Food> feed(T unit, Visibility visibility);
}
