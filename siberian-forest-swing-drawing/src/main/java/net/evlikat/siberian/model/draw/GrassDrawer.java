package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Grass;

import java.awt.Color;
import java.awt.Graphics2D;

import static net.evlikat.siberian.utils.ColorUtils.modify;

public class GrassDrawer implements Drawer<Grass> {

    private static final Color COLOR = new Color(159, 235, 150);

    @Override
    public void draw(Grass grass, Graphics2D g) {
        g.setColor(modify(COLOR, 1 - grass.amountPart()));
        g.fillRect(0, 0, (int) g.getClipBounds().getWidth(), (int) g.getClipBounds().getHeight());
    }
}
