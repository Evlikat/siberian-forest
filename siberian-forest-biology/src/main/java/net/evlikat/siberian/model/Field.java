package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.geo.Sized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Field implements ScentStorage, Sized {

    private final static Logger LOGGER = LoggerFactory.getLogger(Field.class);

    private final int width;
    private final int height;

    private final List<Cell> cells;

    private List<Rabbit> rabbits = new ArrayList<>();
    private final List<Rabbit> justBornRabbits = new ArrayList<>();

    private List<Wolf> wolves = new ArrayList<>();
    private final List<Wolf> justBornWolves = new ArrayList<>();

    public Field(int width, int height,
                 List<Cell> cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public List<Wolf> updateWolves() {
        new ArrayList<>(wolves).forEach(this::unitTurn);
        wolves = wolves.stream()
                .filter(LivingUnit::isAlive)
                .collect(Collectors.toList());
        justBornWolves.forEach(this::addWolf);
        ArrayList<Wolf> backup = new ArrayList<>(justBornWolves);
        justBornWolves.clear();
        return backup;
    }

    public List<Rabbit> updateRabbits() {
        new ArrayList<>(rabbits).forEach(this::unitTurn);
        rabbits = rabbits.stream()
                .filter(LivingUnit::isAlive)
                .collect(Collectors.toList());
        justBornRabbits.forEach(this::addRabbit);
        ArrayList<Rabbit> backup = new ArrayList<>(justBornRabbits);
        justBornRabbits.clear();
        return backup;
    }

    public void updateCells() {
        new ArrayList<>(cells).forEach(this::cellTurn);
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
                Stream.concat(rabbits.stream(), wolves.stream())
                        .filter(u -> u != unit)
                        .filter(u -> u.getPosition().distance(unit.getPosition()) <= unit.getSight())
                        .collect(Collectors.toList()),
                unit.getPosition()
                        .around(unit.getSight(), this)
                        .stream()
                        .map(this::cellOn).collect(Collectors.toSet())
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

    public int rabbitsTotal() {
        return rabbits.size();
    }

    public int wolvesTotal() {
        return wolves.size();
    }

    public void addRabbit(Rabbit rabbit) {
        rabbit.addBirthListener(justBornRabbits::add);
        rabbits.add(rabbit);
    }

    public void addWolf(Wolf wolf) {
        wolf.addBirthListener(justBornWolves::add);
        wolves.add(wolf);
    }

    public Stream<LivingUnit<?>> unitsOn(Position position) {
        return Stream.concat(
                new ArrayList<>(rabbits).stream(),
                new ArrayList<>(wolves).stream()
        ).filter(u -> u.getPosition().equals(position));
    }
}
