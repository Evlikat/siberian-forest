package net.evlikat.siberian.model;

import java.util.Set;

public abstract class AbstractCell<T extends AbstractCell<T>> {

    private final Position position;

    public AbstractCell(Position position) {
        this.position = position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "AbstractCell{" +
                "on " + position +
                '}';
    }
}
