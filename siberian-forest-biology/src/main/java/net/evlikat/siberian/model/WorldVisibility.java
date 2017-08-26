package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldVisibility implements Visibility {

    private final int width;
    private final int height;
    private final Collection<? extends LivingUnitInfo> units;
    private final Collection<Cell> cells;

    public WorldVisibility(int width, int height, Collection<? extends LivingUnitInfo> units, Collection<Cell> cells) {
        this.width = width;
        this.height = height;
        this.units = units;
        this.cells = cells;
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
    public Stream<? extends LivingUnitInfo> units() {
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
