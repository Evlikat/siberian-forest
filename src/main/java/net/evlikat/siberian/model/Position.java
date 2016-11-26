package net.evlikat.siberian.model;

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
