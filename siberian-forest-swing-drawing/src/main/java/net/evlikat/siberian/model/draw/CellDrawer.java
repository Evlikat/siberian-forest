package net.evlikat.siberian.model.draw;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.model.Cell;

import java.awt.Color;
import java.awt.Graphics2D;

public class CellDrawer implements Drawer<Cell> {

    private static final Config CONF = Configuration.ROOT.getConfig("cell");

    public static final int SIZE = CONF.getInt("draw.size");
    private static final Color BORDER = Color.LIGHT_GRAY;

    private final GrassDrawer grassDrawer = new GrassDrawer();

    @Override
    public void draw(Cell cell, Graphics2D g2d) {
        grassDrawer.draw(cell.getGrass(), g2d);
        g2d.setColor(BORDER);
        g2d.drawRect(0, 0, SIZE - 1, SIZE - 1);
    }
}
