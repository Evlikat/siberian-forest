package net.evlikat.siberian.model.draw;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.model.Grass;

import java.awt.Color;
import java.awt.Graphics2D;

import static net.evlikat.siberian.utils.ColorUtils.modify;
import static net.evlikat.siberian.utils.ColorUtils.parse;

public class GrassDrawer implements Drawer<Grass> {

    private static final Config CONF = Configuration.ROOT.getConfig("grass.draw");

    private static final Color COLOR = parse(CONF.getString("color"));

    @Override
    public void draw(Grass grass, Graphics2D g) {
        g.setColor(modify(COLOR, 1 - grass.amountPart()));
        g.fillRect(0, 0, (int) g.getClipBounds().getWidth(), (int) g.getClipBounds().getHeight());
    }
}
