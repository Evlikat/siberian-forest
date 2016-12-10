package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

public interface ZooFactory {

    Rabbit createRabbit(Position position, ScentStorage scentStorage);

    Wolf createWolf(Position position, ScentStorage scentStorage);
}
