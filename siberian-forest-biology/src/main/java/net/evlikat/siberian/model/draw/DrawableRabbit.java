package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Rabbit;

public class DrawableRabbit extends DrawableLivingUnit<Rabbit, RabbitDrawer> {
    public DrawableRabbit(Rabbit object, RabbitDrawer drawer) {
        super(object, drawer);
    }

    public Rabbit getRabbit() {
        return object;
    }
}
