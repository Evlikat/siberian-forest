package net.evlikat.siberian.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldVisibility implements Visibility {

    private final int width;
    private final int height;
    private final List<LivingUnit> units;
    private final List<Cell> cells;

    public WorldVisibility(int width, int height, List<LivingUnit> units, List<Cell> cells) {
        this.width = width;
        this.height = height;
        this.units = units;
        this.cells = cells;
    }

    public static WorldVisibility no() {
        return new WorldVisibility(0, 0, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Stream<LivingUnit> units() {
        return units.stream();
    }

    @Override
    public Stream<Cell> cells() {
        return cells.stream();
    }

    @Override
    public WorldVisibility local(Position p) {
        return new WorldVisibility(
                getWidth(),
                getHeight(),
                units().filter(u -> u.getPosition().equals(p)).collect(Collectors.toList()),
                cells().filter(u -> u.getPosition().equals(p)).collect(Collectors.toList())
        );
    }
}
