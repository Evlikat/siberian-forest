package net.evlikat.siberian.model;

public class CellFactory {

    private static final Drawer<Cell> DRAWER = new CellDrawer();

    public Cell create(Position position, Grass grass) {
        return new Cell(position, grass, DRAWER);
    }
}
