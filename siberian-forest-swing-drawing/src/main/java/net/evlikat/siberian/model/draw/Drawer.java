package net.evlikat.siberian.model.draw;

import java.awt.Graphics2D;

public interface Drawer<T> {

    void draw(T object, Graphics2D g2d);
}
