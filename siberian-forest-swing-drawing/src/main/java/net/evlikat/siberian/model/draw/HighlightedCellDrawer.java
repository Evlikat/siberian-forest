package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.model.Cell;

import java.awt.*;

/**
 * HighlightedCellDrawer
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class HighlightedCellDrawer implements Drawer<Cell> {

    private final CellDrawer cellDrawer;
    private final Color HIGHLIGHT = Color.BLUE;

    public HighlightedCellDrawer(CellDrawer cellDrawer) {
        this.cellDrawer = cellDrawer;
    }

    @Override
    public void draw(Cell cell, Graphics2D g2d) {
        cellDrawer.draw(cell, g2d);

        g2d.setColor(HIGHLIGHT);
        g2d.drawRect(1, 1, CellDrawer.SIZE - 3, CellDrawer.SIZE - 3);
    }
}
