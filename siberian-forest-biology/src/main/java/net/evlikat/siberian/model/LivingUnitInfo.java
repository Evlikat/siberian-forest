package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.utils.stats.NumberGaugeInfo;

import java.util.Optional;

public interface LivingUnitInfo {

    NumberGaugeInfo health();

    boolean adult();

    Optional<Pregnancy> pregnancy();

    Position getPosition();

    Sex sex();
}
