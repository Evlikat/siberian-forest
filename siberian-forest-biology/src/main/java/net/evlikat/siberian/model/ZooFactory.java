package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

public interface ZooFactory {

    Rabbit createRabbit(Position position, RabbitExample example, ScentStorage scentStorage);

    Wolf createWolf(Position position, WolfExample example, ScentStorage scentStorage);
}
