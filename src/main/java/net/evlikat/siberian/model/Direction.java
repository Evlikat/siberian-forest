package net.evlikat.siberian.model;

import javafx.geometry.Pos;

import static net.evlikat.siberian.utils.MathUtils.sgn;

public enum Direction {

    NO(Position.on(0, 0)),
    NORTH(Position.on(0, -1)),
    EAST(Position.on(1, 0)),
    SOUTH(Position.on(0, 1)),
    WEST(Position.on(-1, 0));

    private final Position delta;

    Direction(Position delta) {
        this.delta = delta;
    }

    public int deltaX() {
        return delta.getX();
    }

    public int deltaY() {
        return delta.getY();
    }
}
