package net.evlikat.siberian.model.draw.factory;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.ScentStorage;
import net.evlikat.siberian.model.Wolf;
import net.evlikat.siberian.model.draw.DrawableRabbit;
import net.evlikat.siberian.model.draw.DrawableWolf;

public interface DrawableZooFactory {

    DrawableRabbit createRabbit(Position position, ScentStorage scentStorage);

    DrawableWolf createWolf(Position position, ScentStorage scentStorage);

    DrawableRabbit wrap(Rabbit rabbit);

    DrawableWolf wrap(Wolf wolf);
}
