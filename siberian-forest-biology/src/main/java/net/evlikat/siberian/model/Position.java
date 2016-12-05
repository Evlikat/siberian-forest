package net.evlikat.siberian.model;

import net.evlikat.siberian.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Position {

    private final int x;
    private final int y;

    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position on(int x, int y) {
        return new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position by(Direction direction) {
        return by(direction, 1);
    }

    public Position by(Direction direction, int steps) {
        return new Position(
                getX() + direction.deltaX() * steps,
                getY() + direction.deltaY() * steps
        );
    }

    public boolean adjustableIn(int minX, int minY, int maxX, int maxY) {
        return x >= maxX || x < minX || y >= maxY || y < minY;
    }

    public int distance(Position anotherPosition) {
        return Math.abs(x - anotherPosition.x) + Math.abs(y - anotherPosition.y);
    }

    public Position inDirectionTo(Position to, List<Direction> availableDirections) {
        return availableDirections.stream()
                .map(dir -> Pair.of(dir, to.distance(adjust(dir))))
                .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                .map(Pair::getKey)
                .map(this::adjust)
                .orElse(this);
    }

    public Position awayFrom(Position to, List<Direction> availableDirections) {
        return availableDirections.stream()
                .map(dir -> Pair.of(dir, to.distance(adjust(dir))))
                .max((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                .map(Pair::getKey)
                .map(this::adjust)
                .orElse(this);
    }

    public boolean in(Visibility visibility) {
        return !(x < 0 || x >= visibility.getWidth() || y < 0 || y >= visibility.getHeight());
    }

    public Position adjust(Direction direction) {
        return new Position(x + direction.deltaX(), y + direction.deltaY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position pos = (Position) o;

        return x == pos.x && y == pos.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
