package net.evlikat.siberian.model;

import java.awt.Graphics2D;

public interface DrawableUnit {

    void draw(Graphics2D g);

    Position getPosition();
}
