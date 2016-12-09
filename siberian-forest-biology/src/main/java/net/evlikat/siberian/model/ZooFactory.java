package net.evlikat.siberian.model;

public interface ZooFactory {

    Rabbit createRabbit(Position position, ScentStorage scentStorage);

    Wolf createWolf(Position position, ScentStorage scentStorage);
}
