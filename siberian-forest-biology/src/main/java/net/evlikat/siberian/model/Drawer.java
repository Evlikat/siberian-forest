package net.evlikat.siberian.model;

import java.awt.Graphics2D;

public interface Drawer<T> {

    void draw(T object, Graphics2D graphics2D);
}
