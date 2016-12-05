package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.*;

public class Cell {

    protected static final Config CONF = ConfigFactory.load().getConfig("cell");

    public static final int SIZE = CONF.getInt("draw.size");

    static final Color BORDER = Color.LIGHT_GRAY;

    private final Position position;
    private final Grass grass;
    private final Scent scent;

    public Cell(Position position, Grass grass) {
        this.grass = grass;
        this.position = position;
        this.scent = new Scent();
    }

    public Position getPosition() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public void draw(Graphics2D g) {
        grass.draw(g);
        g.setColor(BORDER);
        g.drawRect(0, 0, SIZE - 1, SIZE - 1);
    }

    public Grass getGrass() {
        return grass;
    }

    public void updateScent() {
        scent.restore();
    }

    public Scent getScent() {
        return scent;
    }

    public void update() {
        grass.update(WorldVisibility.no());
        scent.update();
    }

    @Override
    public String toString() {
        return "Cell{" +
                "position=" + position +
                ", grass=" + grass +
                ", scent=" + scent +
                '}';
    }
}
