package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Cell;

import java.awt.*;

public class DrawableCell extends DrawableObject<Cell, CellDrawer> {

    private Float value;
    private HighlightedCellDrawer hDrawer;

    public DrawableCell(Cell object, CellDrawer drawer, HighlightedCellDrawer hDrawer) {
        super(object, drawer);
        this.hDrawer = hDrawer;
    }

    public Cell getCell() {
        return object;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (value != null) {
            hDrawer.draw(this, g2d);
        } else {
            super.draw(g2d);
        }
    }
}
