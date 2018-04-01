package net.evlikat.siberian.model.draw;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.Cell;
import net.evlikat.siberian.model.Field;
import net.evlikat.siberian.model.LivingUnit;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.RabbitExample;
import net.evlikat.siberian.model.UpdateResult;
import net.evlikat.siberian.model.Wolf;
import net.evlikat.siberian.model.WolfExample;
import net.evlikat.siberian.model.draw.factory.CellFactory;
import net.evlikat.siberian.model.draw.factory.DrawableZooFactory;
import net.evlikat.siberian.model.draw.factory.GrassFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static net.evlikat.siberian.model.draw.CellDrawer.SIZE;

public class DrawableField implements Drawable {

    private final Field field;

    private final DrawableZooFactory drawableZooFactory;

    private final List<DrawableCell> highlightedCells = new ArrayList<>();
    private final Map<Position, DrawableCell> drawableCells;
    private List<DrawableRabbit> drawableRabbits = new ArrayList<>();
    private List<DrawableWolf> drawableWolves = new ArrayList<>();

    private DrawableField(Field field, DrawableZooFactory drawableZooFactory, Map<Position, DrawableCell> drawableCells) {
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
        return new DrawableField(field, drawableZooFactory, drawableCells.stream().collect(toMap(c -> c.getCell().getPosition(), c -> c)));
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
        drawableCells.forEach((pos, cell) -> cell.draw((Graphics2D) g.create(
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

    public void addRabbitOn(Position position, RabbitExample example) {
        DrawableRabbit drawable = drawableZooFactory.createRabbit(position, example, field);
        field.addRabbit(drawable.getRabbit());
        drawableRabbits.add(drawable);
    }

    public void addWolfOn(Position position, WolfExample example) {
        DrawableWolf drawable = drawableZooFactory.createWolf(position, example, field);
        field.addWolf(drawable.getWolf());
        drawableWolves.add(drawable);
    }

    public Stream<LivingUnit<?>> unitsOn(Position position) {
        return field.unitsOn(position);
    }

    public DrawableCell drawableCellOn(Position position) {
        return drawableCells.get(position);
    }

    public void highlightAim(LivingUnit<?> livingUnit) {
        highlightedCells.forEach(c -> c.setValue(null));
        highlightedCells.clear();
        if (livingUnit == null) {
            return;
        }
        Map<Position, Integer> evaluated = livingUnit.evaluate(field.visibilityFor(livingUnit));
        int min = evaluated.values().stream().mapToInt(i -> i).min().orElse(0);
        int max = evaluated.values().stream().mapToInt(i -> i).max().orElse(0);
        evaluated
            .forEach((position, value) -> {
                DrawableCell dc = drawableCellOn(position);
                dc.setValue(min == max ? 1 : ((float) value - min) / (max - min));
                highlightedCells.add(dc);
            });
    }
}
