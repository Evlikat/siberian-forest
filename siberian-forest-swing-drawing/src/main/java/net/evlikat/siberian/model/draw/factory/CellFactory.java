package net.evlikat.siberian.model.draw.factory;

import net.evlikat.siberian.model.Cell;
import net.evlikat.siberian.model.Grass;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.draw.CellDrawer;
import net.evlikat.siberian.model.draw.DrawableCell;
import net.evlikat.siberian.model.draw.HighlightedCellDrawer;

public class CellFactory {

    private static final CellDrawer DRAWER = new CellDrawer();

    public DrawableCell create(Position position, Grass grass) {
        return new DrawableCell(new Cell(position, grass), DRAWER, new HighlightedCellDrawer(DRAWER));
    }
}
