package net.evlikat.siberian.geo;

public abstract class AbstractCell {

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
