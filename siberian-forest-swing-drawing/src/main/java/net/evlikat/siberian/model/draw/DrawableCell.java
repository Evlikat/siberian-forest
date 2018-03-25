package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Cell;

import java.awt.*;

public class DrawableCell extends DrawableObject<Cell, CellDrawer> {

    private boolean isHighlighted = false;
    private HighlightedCellDrawer hDrawer;

    public DrawableCell(Cell object, CellDrawer drawer, HighlightedCellDrawer hDrawer) {
        super(object, drawer);
        this.hDrawer = hDrawer;
    }

    public Cell getCell() {
        return object;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isHighlighted) {
            hDrawer.draw(this.object, g2d);
        } else {
            super.draw(g2d);
        }
    }
}
