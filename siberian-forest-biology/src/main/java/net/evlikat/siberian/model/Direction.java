package net.evlikat.siberian.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

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

    public static Stream<Direction> shuffledValues() {
        ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(values()));
        Collections.shuffle(directions);
        return directions.stream();
    }
}
