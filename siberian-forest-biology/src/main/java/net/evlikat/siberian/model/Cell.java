package net.evlikat.siberian.model;

import java.awt.Graphics2D;

public class Cell extends AbstractCell<Cell> {

    private final Drawer<Cell> drawer;
    private final Grass grass;
    private final Scent scent;

    Cell(Position position, Grass grass, Drawer<Cell> drawer) {
        super(position);
        this.grass = grass;
        this.drawer = drawer;
        this.scent = new Scent();
    }

    void draw(Graphics2D g) {
        drawer.draw(this, g);
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
        grass.update(WorldVisibility.no());
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
