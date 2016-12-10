package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Wolf;

public class DrawableWolf extends DrawableLivingUnit<Wolf, WolfDrawer> {

    public DrawableWolf(Wolf object, WolfDrawer drawer) {
        super(object, drawer);
    }

    public Wolf getWolf() {
        return object;
    }
}
