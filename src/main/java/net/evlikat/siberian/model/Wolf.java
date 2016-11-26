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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Wolf extends LivingUnit implements DrawableUnit {

    private final static Logger LOGGER = LoggerFactory.getLogger(Wolf.class);

    private static final double BIRTH_RATE = 0.3d;
    private static final int ADULT = 15;
    private static final int PREGNANCY_TIME = 30;
    private static final Map<Sex, Color> SEX_COLOR = new EnumMap<>(Sex.class);
    private static final Color LOST_HEALTH = Color.RED;
    private static final int FETUS_SIZE = 5;
    private static final int SIZE = 20;

    static {
        SEX_COLOR.put(Sex.MALE, Color.ORANGE);
        SEX_COLOR.put(Sex.FEMALE, Color.BLUE);
    }

    private final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();

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
        Optional.ofNullable(pregnancy)
                .ifPresent(gauge -> g.fillRect(SIZE - FETUS_SIZE, SIZE, FETUS_SIZE - 1, FETUS_SIZE - 1));
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
        if (this.sex != Sex.FEMALE || this.age.getCurrent() < ADULT || pregnancy != null) {
            return;
        }
        neighbours.stream()
                .map(unit -> unit instanceof Wolf ? (Wolf) unit : null)
                .filter(Objects::nonNull)
                .filter(otherWolf -> otherWolf.sex != this.sex && otherWolf.age.getCurrent() > ADULT)
                .findAny()
                .ifPresent(mate -> pregnancy = Optional.of(new Pregnancy(PREGNANCY_TIME)));
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
        pregnancy.ifPresent(p -> {
            if (p.incAndWhelp(g -> {
                do {
                    birth(new Wolf(getPosition(), Sex.random()));
                    LOGGER.debug("New wolf was born on {}", getPosition());
                } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
            })) {
                pregnancy = Optional.empty();
            }
        });
    }
}
