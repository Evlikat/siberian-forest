package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.geo.Sized;
import net.evlikat.siberian.model.draw.DrawableCell;
import net.evlikat.siberian.model.draw.DrawableRabbit;
import net.evlikat.siberian.model.draw.DrawableWolf;
import net.evlikat.siberian.model.draw.factory.CellFactory;
import net.evlikat.siberian.model.draw.factory.DrawableZooFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.draw.CellDrawer.SIZE;
import static net.evlikat.siberian.utils.CollectionUtils.throwingMerger;

public class Field implements ScentStorage, Sized {

    private final static Logger LOGGER = LoggerFactory.getLogger(Field.class);

    private static final CellFactory cellFactory = new CellFactory();

    private final int width;
    private final int height;

    private final DrawableZooFactory drawableZooFactory;

    private final List<Cell> cells;
    private final List<DrawableCell> drawableCells;

    private LinkedHashMap<Rabbit, DrawableRabbit> rabbits = new LinkedHashMap<>();
    private final LinkedHashMap<Rabbit, DrawableRabbit> justBornRabbits = new LinkedHashMap<>();

    private LinkedHashMap<Wolf, DrawableWolf> wolves = new LinkedHashMap<>();
    private final LinkedHashMap<Wolf, DrawableWolf> justBornWolves = new LinkedHashMap<>();

    private Field(int width, int height,
                  DrawableZooFactory drawableZooFactory,
                  List<Cell> cells, List<DrawableCell> drawableCells) {
        this.width = width;
        this.height = height;
        this.drawableZooFactory = drawableZooFactory;
        this.cells = cells;
        this.drawableCells = drawableCells;
    }

    public static Field create(int width, int height, DrawableZooFactory drawableZooFactory) {
        ArrayList<DrawableCell> drawableCells = new ArrayList<>(width * height);
        IntStream.range(0, width * height)
                .forEach(i -> {
                    Position position = Position.on(i % width, i / width);
                    drawableCells.add(cellFactory.create(position, new Grass(position)));
                });
        List<Cell> cells = drawableCells.stream().map(DrawableCell::getCell).collect(Collectors.toList());
        return new Field(width, height, drawableZooFactory, cells, drawableCells);
    }

    public UpdateResult update() {
        long st = System.currentTimeMillis();
        new ArrayList<>(cells).forEach(this::cellTurn);

        new LinkedHashMap<>(rabbits).keySet().forEach(this::unitTurn);
        rabbits = rabbits.entrySet().stream()
                .filter(e -> e.getKey().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, throwingMerger(), LinkedHashMap::new));
        justBornRabbits.values().forEach(r -> addRabbit(r, drawableZooFactory::wrap));
        justBornRabbits.clear();

        new LinkedHashMap<>(wolves).keySet().forEach(this::unitTurn);
        wolves = wolves.entrySet().stream()
                .filter(e -> e.getKey().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, throwingMerger(), LinkedHashMap::new));
        justBornWolves.values().forEach(w -> addWolf(w, drawableZooFactory::wrap));
        justBornWolves.clear();

        long end = System.currentTimeMillis();
        return new UpdateResult(end - st, rabbits.size(), wolves.size());
    }

    private void cellTurn(Cell cell) {
        cell.update();
    }

    private void unitTurn(LivingUnit<?> unit) {
        unit.update(this::visibilityFor);
    }

    public WorldVisibility visibilityFor(LivingUnit<?> unit) {
        return new WorldVisibility(
                getWidth(),
                getHeight(),
                Stream.concat(rabbits.keySet().stream(), wolves.keySet().stream())
                        .filter(u -> u != unit)
                        .filter(u -> u.getPosition().distance(unit.getPosition()) <= unit.getSight())
                        .collect(Collectors.toList()),
                unit.getPosition()
                        .around(unit.getSight(), this)
                        .stream()
                        .map(this::cellOn).collect(Collectors.toSet())
        );
    }

    public void draw(Graphics2D g) {
        drawableCells.forEach(cell -> cell.draw((Graphics2D) g.create(
                cell.getCell().getX() * SIZE,
                cell.getCell().getY() * SIZE,
                SIZE - 1,
                SIZE - 1)
        ));

        Stream.concat(
                new LinkedHashMap<>(rabbits).entrySet().stream(),
                new LinkedHashMap<>(wolves).entrySet().stream()
        ).collect(
                Collectors.groupingBy(du -> du.getKey().getPosition())
        ).forEach((position, units) -> {
                    units.stream().filter(u -> u.getKey() instanceof Wolf).findAny().ifPresent(unit ->
                            unit.getValue().draw((Graphics2D) g.create(
                                    position.getX() * SIZE,
                                    position.getY() * SIZE,
                                    SIZE - 1,
                                    SIZE - 1)
                            )
                    );
                    units.stream().filter(u -> u.getKey() instanceof Rabbit).findAny().ifPresent(unit ->
                            unit.getValue().draw((Graphics2D) g.create(
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

    @Override
    public Scent get(Position position) {
        return cellOn(position).getScent();
    }

    @Override
    public void update(Position position) {
        cellOn(position).updateScent();
    }

    private Cell cellOn(Position position) {
        return cells.get(position.getY() * width + position.getX());
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void addRabbitOn(Position position) {
        addRabbit(drawableZooFactory.createRabbit(position, this), drawableZooFactory::wrap);
    }

    public void addWolfOn(Position position) {
        addWolf(drawableZooFactory.createWolf(position, this), drawableZooFactory::wrap);
    }

    private void addRabbit(DrawableRabbit drawableRabbit, Function<Rabbit, DrawableRabbit> handler) {
        drawableRabbit.getRabbit().addBirthListener(b -> justBornRabbits.put(b, handler.apply(b)));
        rabbits.put(drawableRabbit.getLivingUnit(), drawableRabbit);
    }

    private void addWolf(DrawableWolf drawableWolf, Function<Wolf, DrawableWolf> handler) {
        drawableWolf.getWolf().addBirthListener(b -> justBornWolves.put(b, handler.apply(b)));
        wolves.put(drawableWolf.getLivingUnit(), drawableWolf);
    }

    public Stream<LivingUnit<?>> unitsOn(Position position) {
        return Stream.concat(
                new LinkedHashMap<>(rabbits).keySet().stream(),
                new LinkedHashMap<>(wolves).keySet().stream()
        ).filter(u -> u.getPosition().equals(position));
    }
}
