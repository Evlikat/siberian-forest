package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

public class RegularZooFactory implements ZooFactory {

    @Override
    public Rabbit createRabbit(Position position, RabbitExample example, ScentStorage scentStorage) {
        return new Rabbit(new RegularRabbitAI(), position, example.getAge(), example.getSex(), scentStorage);
    }

    @Override
    public Wolf createWolf(Position position, WolfExample example, ScentStorage scentStorage) {
        return new Wolf(new RegularWolfAI(), position, example.getAge(), example.getSex(), scentStorage);
    }
}
