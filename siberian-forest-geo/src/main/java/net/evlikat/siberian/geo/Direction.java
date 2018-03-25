package net.evlikat.siberian.geo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

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

    private static Direction byRelativePosition(Position pos) {
        return stream(values()).filter(d -> d.delta.equals(pos)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public int deltaX() {
        return delta.getX();
    }

    public int deltaY() {
        return delta.getY();
    }

    public Direction opposite() {
        return byRelativePosition(Position.on(-this.delta.getX(), -this.delta.getY()));
    }

    public static Stream<Direction> shuffledValues() {
        ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(values()));
        Collections.shuffle(directions);
        return directions.stream();
    }
}
