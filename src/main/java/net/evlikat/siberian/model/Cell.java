package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.*;

public class Cell implements DrawableUnit {

    protected static final Config CONF = ConfigFactory.load().getConfig("cell");

    public static final int SIZE = CONF.getInt("draw.size");

    static final Color BORDER = Color.LIGHT_GRAY;

    private final Position position;

    public Cell(int x, int y) {
        this.position = Position.on(x, y);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    @Override
    public void update(Visibility visibility) {
        // nothing changes
    }

    public void draw(Graphics2D g) {
        g.setColor(BORDER);
        g.drawRect(getX() * SIZE, getY() * SIZE, SIZE - 1, SIZE - 1);
    }
}
