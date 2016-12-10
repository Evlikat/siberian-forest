package net.evlikat.siberian.model.draw;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.Wolf;

import java.awt.Color;
import java.awt.Graphics2D;

public class WolfDrawer implements Drawer<Wolf> {

    protected static final Config CONF = ConfigFactory.load().getConfig("wolf.draw");

    protected static final int FETUS_SIZE = CONF.getInt("fetus.size");
    protected static final int SIZE = CONF.getInt("size");

    private static final Color LOST_HEALTH = Color.GRAY;

    @Override
    public void draw(Wolf wolf, Graphics2D g) {
        int size = wolf.adult() ? SIZE : (SIZE / 2);

        int xPadding = (int) (g.getClipBounds().getWidth() - size) / 2;
        int yPadding = (int) (g.getClipBounds().getHeight() - size) / 2;

        g.setColor(LOST_HEALTH);
        int healthyArcAngle = (int) (wolf.health().part() * 360);
        g.fillArc(xPadding, yPadding, size - 1, size - 1, 90 + healthyArcAngle, 360 - healthyArcAngle);
        g.setColor(wolf.sex().getColor());
        g.fillArc(xPadding, yPadding, size - 1, size - 1, 90, healthyArcAngle);
        wolf.pregnancy().ifPresent(gauge -> g.fillRect(size - FETUS_SIZE, 0, FETUS_SIZE - 1, FETUS_SIZE - 1));
    }
}
