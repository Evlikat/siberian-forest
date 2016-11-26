package net.evlikat.siberian.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Wolf extends LivingUnit implements DrawableUnit {

    private final static Logger LOGGER = LoggerFactory.getLogger(Wolf.class);

    private static final double BIRTH_RATE = 0.3d;
    private static final int ADULT = 15;
    private static final Map<Sex, Color> SEX_COLOR = new EnumMap<>(Sex.class);
    private static final Color LOST_HEALTH = Color.RED;
    private static final int SIZE = 20;

    static {
        SEX_COLOR.put(Sex.MALE, Color.ORANGE);
        SEX_COLOR.put(Sex.FEMALE, Color.BLUE);
    }

    private final Sex sex;

    public Wolf(Position position, Sex sex) {
        super(3, position, Collections.singletonList(Rabbit.class));
        this.sex = sex;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(LOST_HEALTH);
        g.fillOval(0, 0, SIZE - 1, SIZE - 1);
        g.setColor(SEX_COLOR.get(sex));
        g.fillArc(0, 0, SIZE - 1, SIZE - 1, 90, (int) (health.part() * 360));
    }

    @Override
    public void update(Visibility visibility) {
        update();

        List<Position> availableDirections = Arrays.stream(Direction.values())
                .map(dir -> getPosition().by(dir))
                .filter(newPos -> !newPos.adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                .collect(Collectors.toList());

        if (availableDirections.isEmpty()) {
            return;
        }
        Position newPosition = availableDirections
                .get(ThreadLocalRandom.current().nextInt(availableDirections.size()));
        setPosition(newPosition);

        List<LivingUnit> neighbours = visibility.units()
                .filter(obj -> obj.getPosition().equals(getPosition()))
                .collect(Collectors.toList());
        eat(neighbours);
        multiply(neighbours);
    }

    private void multiply(List<LivingUnit> neighbours) {
        if (this.sex != Sex.FEMALE || this.age.getCurrent() < ADULT) {
            return;
        }
        neighbours.stream()
                .filter(unit -> {
                    if (unit instanceof Wolf) {
                        Wolf otherWolf = (Wolf) unit;
                        return otherWolf.sex != this.sex;
                    }
                    return false;
                })
                .findAny()
                .ifPresent(mate -> {
                    do {
                        birth(new Wolf(getPosition(), Sex.random()));
                        LOGGER.debug("New wolf was born on {}", getPosition());
                    } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
                });
    }

    private void eat(List<LivingUnit> neighbours) {
        neighbours.stream()
                .filter(unit -> this.canEat(unit.getClass()))
                .findAny()
                .ifPresent(unit -> {
                    if (unit.kill()) {
                        LOGGER.debug("A Wolf[{}] on {} is eating", health, getPosition());
                        health.setCurrent(health.getCurrent() + Rabbit.FOOD_VALUE);
                    }
                });
    }

    protected void updateGauges() {
        health.dec();
    }
}
