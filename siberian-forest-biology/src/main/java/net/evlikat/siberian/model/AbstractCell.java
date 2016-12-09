package net.evlikat.siberian.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCell<T extends AbstractCell<T>> {

    private final Position position;
    private final Set<T> neighbours = new HashSet<>();

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

    protected Set<T> nearestNeighbours() {
        return neighbours;
    }

    public Set<T> neighbours(int radius) {
        HashSet<T> result = new HashSet<>();
        LinkedList<T> queue = new LinkedList<>(neighbours);
        T cell;
        while ((cell = queue.poll()) != null) {
            if (position.distance(cell.getPosition()) <= radius) {
                result.add(cell);
            }
            Set<T> newCells = cell.nearestNeighbours().stream()
                    .filter(c -> position.distance(c.getPosition()) <= radius)
                    .filter(c -> !result.contains(c) && c != this)
                    .collect(Collectors.toSet());

            result.addAll(newCells);
            queue.addAll(newCells);
        }
        return result;
    }

    void addNeighbour(T cell) {
        if (this.equals(cell)) {
            return;
        }
        neighbours.add(cell);
    }

    @Override
    public String toString() {
        return "AbstractCell{" +
                "on " + position +
                '}';
    }
}
