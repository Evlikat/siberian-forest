package net.evlikat.siberian.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Wolf extends LivingUnit implements DrawableUnit {

    private static final double BIRTH_RATE = 0.3d;
    private static final Color COLOR = Color.ORANGE;
    private static final Color LOST_HEALTH = Color.RED;
    private static final int SIZE = 20;

    private final Sex sex;

    public Wolf(Position position, Sex sex) {
        super(3, position, Collections.singletonList(Rabbit.class));
        this.sex = sex;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(LOST_HEALTH);
        g.fillOval(0, 0, SIZE - 1, SIZE - 1);
        g.setColor(COLOR);
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
        neighbours.stream()
                .filter(unit -> this.canEat(unit.getClass()))
                .findAny()
                .ifPresent(unit -> {
                    unit.kill();
                    health.setCurrent(health.getCurrent() + 50);
                });
        if (this.sex == Sex.FEMALE) {
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
                        } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
                    });
        }
    }

    protected void updateGauges() {
        health.setCurrent(health.getCurrent() - 1);
    }
}
