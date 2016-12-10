package net.evlikat.siberian.model.draw;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.Rabbit;

import java.awt.Color;
import java.awt.Graphics2D;

public class RabbitDrawer implements Drawer<Rabbit> {

    private static final Config CONF = ConfigFactory.load().getConfig("rabbit.draw");

    private static final int SIZE = CONF.getInt("size");
    private static final int FETUS_SIZE = CONF.getInt("fetus.size");
    private static final Color BORDER = new Color(117, 66, 16);

    @Override
    public void draw(Rabbit rabbit, Graphics2D g) {
        int size = rabbit.adult() ? SIZE : (SIZE / 2);

        int xPadding = (int) (g.getClipBounds().getWidth() - size) / 2;
        int yPadding = (int) (g.getClipBounds().getHeight() - size) / 2;

        g.setColor(BORDER);
        g.fillRect(xPadding, yPadding, size - 1, size - 1);
        rabbit.pregnancy()
                .ifPresent(gauge -> g.fillRect(size - FETUS_SIZE, 0, FETUS_SIZE - 1, FETUS_SIZE - 1));
    }
}
