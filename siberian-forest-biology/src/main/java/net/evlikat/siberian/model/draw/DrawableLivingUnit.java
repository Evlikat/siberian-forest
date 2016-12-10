package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.LivingUnit;

public class DrawableLivingUnit<T extends LivingUnit, D extends Drawer<T>> extends DrawableObject<T, D> {

    public DrawableLivingUnit(T object, D drawer) {
        super(object, drawer);
    }

    public T getLivingUnit() {
        return object;
    }
}
