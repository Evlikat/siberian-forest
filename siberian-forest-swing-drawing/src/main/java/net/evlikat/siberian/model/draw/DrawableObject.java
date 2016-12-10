package net.evlikat.siberian.model.draw;

import java.awt.Graphics2D;

public class DrawableObject<T, D extends Drawer<T>> implements Drawable {

    protected final T object;
    protected final D drawer;

    public DrawableObject(T object, D drawer) {
        this.object = object;
        this.drawer = drawer;
    }

    @Override
    public void draw(Graphics2D g2d) {
        drawer.draw(object, g2d);
    }
}
