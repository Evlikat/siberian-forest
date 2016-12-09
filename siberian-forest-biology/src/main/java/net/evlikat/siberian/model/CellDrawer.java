package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.Color;
import java.awt.Graphics2D;

public class CellDrawer implements Drawer<Cell> {

    protected static final Config CONF = ConfigFactory.load().getConfig("cell");

    public static final int SIZE = CONF.getInt("draw.size");
    static final Color BORDER = Color.LIGHT_GRAY;

    @Override
    public void draw(Cell cell, Graphics2D g2d) {
        cell.getGrass().draw(g2d);
        g2d.setColor(BORDER);
        g2d.drawRect(0, 0, SIZE - 1, SIZE - 1);
    }
}
