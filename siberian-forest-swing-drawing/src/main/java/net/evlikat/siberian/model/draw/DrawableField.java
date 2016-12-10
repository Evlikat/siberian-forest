package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.Cell;
import net.evlikat.siberian.model.Field;
import net.evlikat.siberian.model.Grass;
import net.evlikat.siberian.model.LivingUnit;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.UpdateResult;
import net.evlikat.siberian.model.Wolf;
import net.evlikat.siberian.model.draw.factory.CellFactory;
import net.evlikat.siberian.model.draw.factory.DrawableZooFactory;
import net.evlikat.siberian.model.draw.factory.GrassFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.draw.CellDrawer.SIZE;

public class DrawableField implements Drawable {

    private final Field field;

    private final DrawableZooFactory drawableZooFactory;

    private final List<DrawableCell> drawableCells;
    private List<DrawableRabbit> drawableRabbits = new ArrayList<>();
    private List<DrawableWolf> drawableWolves = new ArrayList<>();

    public DrawableField(Field field, DrawableZooFactory drawableZooFactory, List<DrawableCell> drawableCells) {
        this.field = field;
        this.drawableZooFactory = drawableZooFactory;
        this.drawableCells = drawableCells;
    }

    public static DrawableField create(int width, int height,
                                       CellFactory cellFactory,
                                       GrassFactory grassFactory,
                                       DrawableZooFactory drawableZooFactory) {
        ArrayList<DrawableCell> drawableCells = new ArrayList<>(width * height);
        IntStream.range(0, width * height)
                .forEach(i -> {
                    Position position = Position.on(i % width, i / width);
                    drawableCells.add(cellFactory.create(position, grassFactory.createGrass()));
                });
        List<Cell> cells = drawableCells.stream().map(DrawableCell::getCell).collect(Collectors.toList());
        Field field = new Field(width, height, cells);
        return new DrawableField(field, drawableZooFactory, drawableCells);
    }

    public UpdateResult update() {
        long st = System.currentTimeMillis();
        field.updateCells();

        List<Rabbit> newRabbits = field.updateRabbits();
        drawableRabbits = drawableRabbits.stream().filter(du -> du.getRabbit().isAlive()).collect(Collectors.toList());
        newRabbits.forEach(newRabbit -> drawableRabbits.add(drawableZooFactory.wrap(newRabbit)));

        List<Wolf> newWolves = field.updateWolves();
        drawableWolves = drawableWolves.stream().filter(du -> du.getWolf().isAlive()).collect(Collectors.toList());
        newWolves.forEach(newWolf -> drawableWolves.add(drawableZooFactory.wrap(newWolf)));

        long end = System.currentTimeMillis();
        return new UpdateResult(end - st, field.rabbitsTotal(), field.wolvesTotal());
    }

    @Override
    public void draw(Graphics2D g) {
        drawableCells.forEach(cell -> cell.draw((Graphics2D) g.create(
                cell.getCell().getX() * SIZE,
                cell.getCell().getY() * SIZE,
                SIZE - 1,
                SIZE - 1)
        ));

        Stream.concat(
                new ArrayList<>(drawableRabbits).stream(),
                new ArrayList<>(drawableWolves).stream()
        ).collect(
                Collectors.groupingBy(du -> du.getLivingUnit().getPosition())
        ).forEach((position, units) -> {
                    units.stream().filter(u -> u.getLivingUnit() instanceof Wolf).findAny().ifPresent(unit ->
                            unit.draw((Graphics2D) g.create(
                                    position.getX() * SIZE,
                                    position.getY() * SIZE,
                                    SIZE - 1,
                                    SIZE - 1)
                            )
                    );
                    units.stream().filter(u -> u.getLivingUnit() instanceof Rabbit).findAny().ifPresent(unit ->
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

    public void addRabbitOn(Position position) {
        DrawableRabbit drawable = drawableZooFactory.createRabbit(position, field);
        field.addRabbit(drawable.getRabbit());
        drawableRabbits.add(drawable);
    }

    public void addWolfOn(Position position) {
        DrawableWolf drawable = drawableZooFactory.createWolf(position, field);
        field.addWolf(drawable.getWolf());
        drawableWolves.add(drawable);
    }

    public Stream<LivingUnit<?>> unitsOn(Position position) {
        return field.unitsOn(position);
    }
}
