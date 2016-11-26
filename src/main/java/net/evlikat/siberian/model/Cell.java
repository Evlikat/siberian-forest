package net.evlikat.siberian.model;

import java.awt.*;

public class Cell implements DrawableUnit {

    public static final int SIZE = 20;
    public static final Color BORDER = Color.LIGHT_GRAY;

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
