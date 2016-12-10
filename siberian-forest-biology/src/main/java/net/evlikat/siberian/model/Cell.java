package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.AbstractCell;
import net.evlikat.siberian.geo.Position;

public class Cell extends AbstractCell {

    private final Grass grass;
    private final Scent scent;

    public Cell(Position position, Grass grass) {
        super(position);
        this.grass = grass;
        this.scent = new Scent();
    }

    public Grass getGrass() {
        return grass;
    }

    public void updateScent() {
        scent.restore();
    }

    public Scent getScent() {
        return scent;
    }

    public void update() {
        grass.update();
        scent.update();
    }

    @Override
    public String toString() {
        return "Cell{" +
                "position=" + getPosition() +
                ", grass=" + grass +
                ", scent=" + scent +
                '}';
    }
}
