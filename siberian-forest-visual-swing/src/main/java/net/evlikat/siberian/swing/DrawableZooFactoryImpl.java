package net.evlikat.siberian.swing;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.RabbitExample;
import net.evlikat.siberian.model.RegularZooFactory;
import net.evlikat.siberian.model.ScentStorage;
import net.evlikat.siberian.model.Wolf;
import net.evlikat.siberian.model.WolfExample;
import net.evlikat.siberian.model.ZooFactory;
import net.evlikat.siberian.model.draw.DrawableRabbit;
import net.evlikat.siberian.model.draw.DrawableWolf;
import net.evlikat.siberian.model.draw.RabbitDrawer;
import net.evlikat.siberian.model.draw.WolfDrawer;
import net.evlikat.siberian.model.draw.factory.DrawableZooFactory;

public class DrawableZooFactoryImpl implements DrawableZooFactory {

    private final RabbitDrawer rabbitDrawer = new RabbitDrawer();
    private final WolfDrawer wolfDrawer = new WolfDrawer();
    private final ZooFactory zooFactory = new RegularZooFactory();

    @Override
    public DrawableRabbit createRabbit(Position position, RabbitExample example, ScentStorage scentStorage) {
        return new DrawableRabbit(zooFactory.createRabbit(position, example, scentStorage), rabbitDrawer);
    }

    @Override
    public DrawableWolf createWolf(Position position, WolfExample wolfExample, ScentStorage scentStorage) {
        return new DrawableWolf(zooFactory.createWolf(position, wolfExample, scentStorage), wolfDrawer);
    }

    @Override
    public DrawableRabbit wrap(Rabbit rabbit) {
        return new DrawableRabbit(rabbit, rabbitDrawer);
    }

    @Override
    public DrawableWolf wrap(Wolf wolf) {
        return new DrawableWolf(wolf, wolfDrawer);
    }
}
