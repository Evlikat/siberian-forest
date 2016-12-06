package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Rabbit extends LivingUnit implements Food {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rabbit.class);

    private static final Config CONF = ConfigFactory.load().getConfig("rabbit");

    private static final int MAX_AGE = CONF.getInt("maxAge");
    private static final int ADULT = CONF.getInt("adult");
    private static final int SPEED = CONF.getInt("speed");
    private static final int PREGNANCY_TIME = CONF.getInt("pregnancyTime");
    private static final int BIRTH_RATE = CONF.getInt("birthRate");
    private static final int FOOD_VALUE = CONF.getInt("foodValue");

    private static final int SIZE = CONF.getInt("draw.size");
    private static final int FETUS_SIZE = CONF.getInt("draw.fetus.size");
    private static final Color BORDER = new Color(117, 66, 16);

    private final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();

    public Rabbit(Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(3, new NumberGauge(age, 0, MAX_AGE), SPEED, position, Collections.emptyList(), scentStorage);
        this.sex = sex;
    }

    protected Optional<Pregnancy> pregnancy() {
        return pregnancy;
    }

    @Override
    public void draw(Graphics2D g) {
        int size = adult() ? SIZE : (SIZE / 2);

        int xPadding = (int) (g.getClipBounds().getWidth() - size) / 2;
        int yPadding = (int) (g.getClipBounds().getHeight() - size) / 2;

        g.setColor(BORDER);
        g.fillRect(xPadding, yPadding, size - 1, size - 1);
        pregnancy.ifPresent(gauge -> g.fillRect(size - FETUS_SIZE, 0, FETUS_SIZE - 1, FETUS_SIZE - 1));
    }

    @Override
    public int getFoodValue() {
        return FOOD_VALUE;
    }

    @Override
    public boolean eaten() {
        return kill();
    }

    @Override
    protected void multiply(Visibility visibility) {
        if (this.sex != Sex.FEMALE || !adult() || pregnancy().isPresent()) {
            return;
        }
        visibility.units()
                .map(unit -> unit instanceof Rabbit ? (Rabbit) unit : null)
                .filter(Objects::nonNull)
                .filter(otherRabbit -> otherRabbit.sex != this.sex && otherRabbit.adult())
                .findAny()
                .ifPresent(mate -> pregnancy = Optional.of(new Pregnancy(PREGNANCY_TIME)));
    }

    private boolean adult() {
        return age.getCurrent() >= ADULT;
    }

    protected abstract Rabbit newRabbit();

    protected void updateGauges() {
        health.minus(1);
        pregnancy.ifPresent(p -> {
            if (p.incAndWhelp(g -> {
                do {
                    birth(newRabbit());
                    health.minus(50);
                    LOGGER.debug("A new rabbit was born on {}", getPosition());
                } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
            })) {
                pregnancy = Optional.empty();
            }
        });
    }
}
