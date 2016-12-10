package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Cell;

import java.awt.Graphics2D;

public class DrawableCell extends DrawableObject<Cell, CellDrawer> {
    public DrawableCell(Cell object, CellDrawer drawer) {
        super(object, drawer);
    }

    public Cell getCell() {
        return object;
    }
}
