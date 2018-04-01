package net.evlikat.siberian.model.draw;

import java.awt.*;

import static net.evlikat.siberian.utils.ColorUtils.between;

/**
 * HighlightedCellDrawer
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class HighlightedCellDrawer implements Drawer<DrawableCell> {

    private final CellDrawer cellDrawer;
    private final Color HIGHLIGHT_BETTER = new Color(0x358F2B);
    private final Color HIGHLIGHT_WORSE = Color.RED;

    public HighlightedCellDrawer(CellDrawer cellDrawer) {
        this.cellDrawer = cellDrawer;
    }

    @Override
    public void draw(DrawableCell drawableCell, Graphics2D g2d) {
        cellDrawer.draw(drawableCell.getCell(), g2d);

        g2d.setColor(between(HIGHLIGHT_WORSE, HIGHLIGHT_BETTER, drawableCell.getValue()));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(1, 1, CellDrawer.SIZE - 4, CellDrawer.SIZE - 4);
    }
}
