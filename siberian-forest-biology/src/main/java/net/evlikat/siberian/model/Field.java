package net.evlikat.siberian.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.evlikat.siberian.model.Cell.SIZE;

public class Field {

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
                .forEach(i -> {
                    Position position = Position.on(i % width, i / width);
                    cells.add(new Cell(position, new Grass(position)));
                });
        return new Field(width, height, cells);
    }

    public void update() {
        turn++;
        new ArrayList<>(cells).forEach(this::cellTurn);
        new ArrayList<>(units).forEach(this::unitTurn);
        units = units.stream().filter(LivingUnit::isAlive).collect(Collectors.toCollection(ArrayList::new));
        justBornUnits.forEach(this::addUnit);
        justBornUnits.clear();
    }

    private void cellTurn(Cell cell) {
        cell.update();
    }

    private void unitTurn(LivingUnit unit) {
        unit.update(this::visibilityFor);
    }

    public WorldVisibility visibilityFor(LivingUnit unit) {
        return new WorldVisibility(
                getWidth(),
                getHeight(),
                units.stream()
                        .filter(u -> u != unit)
                        .filter(u -> u.getPosition().distance(unit.getPosition()) <= unit.getSight())
                        .collect(Collectors.toList()),
                cells.stream()
                        .filter(c -> c.getPosition().distance(unit.getPosition()) <= unit.getSight())
                        .collect(Collectors.toList()));
    }

    public void draw(Graphics2D g) {
        cells.forEach(cell -> cell.draw((Graphics2D) g.create(
                cell.getX() * SIZE,
                cell.getY() * SIZE,
                SIZE - 1,
                SIZE - 1)
        ));
        Map<Position, List<LivingUnit>> unitsByPosition =
                new ArrayList<>(units).stream().collect(Collectors.groupingBy(LivingUnit::getPosition));
        unitsByPosition.forEach((position, units) -> {
                    units.stream().filter(u -> u instanceof Wolf).findAny().ifPresent(unit ->
                            unit.draw((Graphics2D) g.create(
                                    position.getX() * SIZE,
                                    position.getY() * SIZE,
                                    SIZE - 1,
                                    SIZE - 1)
                            )
                    );
                    units.stream().filter(u -> u instanceof Rabbit).findAny().ifPresent(unit ->
                            unit.draw((Graphics2D) g.create(
                                    position.getX() * SIZE,
                                    position.getY() * SIZE,
                                    SIZE - 1,
                                    SIZE - 1)
                            )
                    );
                    if (units.size() > 1) {
                        g.setColor(Color.BLACK);
                        g.drawString(Integer.toString(units.size()),
                                position.getX() * SIZE,
                                (position.getY() + 1) * SIZE);
                    }
                }
        );
    }

    private int getWidth() {
        return width;
    }

    private int getHeight() {
        return height;
    }

    public void addUnit(LivingUnit livingUnit) {
        livingUnit.addBirthListener(justBornUnits::add);
        units.add(livingUnit);
    }
}
