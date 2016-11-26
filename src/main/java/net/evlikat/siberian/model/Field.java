package net.evlikat.siberian.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.Cell.SIZE;

public class Field implements Visibility {

    private final static Logger LOGGER = LoggerFactory.getLogger(Field.class);

    private long turn = 0;

    private final int width;
    private final int height;

    private final List<Cell> cells;
    private List<LivingUnit> units = new ArrayList<>();
    private final List<LivingUnit> justBornUnits = new ArrayList<>();

    private Field(int width, int height, List<Cell> cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public static Field create(int width, int height) {
        ArrayList<Cell> cells = new ArrayList<>(width * height);
        IntStream.range(0, width * height)
                .forEach(i -> cells.add(new Cell(i % width, i / width)));
        return new Field(width, height, cells);
    }

    public void update() {
        turn++;
        new ArrayList<>(units).forEach((drawableUnit) -> {
            // TODO: consider 'sight'
            drawableUnit.update(this);
        });
        units = units.stream().filter(LivingUnit::isAlive).collect(Collectors.toCollection(ArrayList::new));
        justBornUnits.forEach(this::addUnit);
        justBornUnits.clear();
    }

    public void draw(Graphics2D g) {
        cells.forEach(cell -> cell.draw(g));
        new ArrayList<>(units).forEach(unit ->
                unit.draw((Graphics2D) g.create(
                        unit.getPosition().getX() * SIZE,
                        unit.getPosition().getY() * SIZE,
                        SIZE - 1,
                        SIZE - 1)
                )
        );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Stream<LivingUnit> units() {
        return units.stream();
    }

    public Cell get(int x, int y) {
        return cells.get(y * width + x);
    }

    public Cell set(int x, int y) {
        return cells.set(y * width + x, new Cell(x, y));
    }

    public void addUnit(LivingUnit livingUnit) {
        livingUnit.addBirthListener(justBornUnits::add);
        units.add(livingUnit);
    }
}
