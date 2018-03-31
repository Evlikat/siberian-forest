package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.utils.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class Wolf extends LivingUnit<Wolf> implements WolfInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wolf.class);

    private static final Config CONF = Configuration.ROOT.getConfig("wolf");

    public static final int SIGHT = CONF.getInt("sight");
    public static final int SPEED = CONF.getInt("speed");
    public static final int MAX_AGE = CONF.getInt("maxAge");
    public static final double BIRTH_RATE = CONF.getDouble("birthRate");
    public static final int ADULT = CONF.getInt("adult");
    public static final int PREGNANCY_TIME = CONF.getInt("pregnancyTime");

    protected final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();
    private final AI<WolfInfo> ai;

    public Wolf(AI<WolfInfo> ai, Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(SIGHT, new NumberGauge(age, 0, MAX_AGE), SPEED, position, Collections.singletonList(Rabbit.class), scentStorage);
        this.ai = ai;
        this.sex = sex;
    }

    @Override
    public Optional<Pregnancy> pregnancy() {
        return pregnancy;
    }

    @Override
    public Sex sex() {
        return sex;
    }

    @Override
    public List<Position> aim(Visibility visibility) {
        return ai.aim(this, visibility);
    }

    @Override
    public Optional<Position> move(Visibility visibility) {
        return ai.move(this, visibility);
    }

    @Override
    public Optional<Food> feed(Visibility visibility) {
        return ai.feed(this, visibility);
    }

    @Override
    public void breed(Visibility visibility) {
        if (this.sex != Sex.FEMALE || !adult() || pregnancy().isPresent()) {
            return;
        }
        visibility.units()
            .map(unit -> unit instanceof Wolf ? (Wolf) unit : null)
            .filter(Objects::nonNull)
            .filter(otherWolf -> otherWolf.sex != this.sex && otherWolf.adult())
            .findAny()
            .ifPresent(mate -> pregnancy = Optional.of(new Pregnancy(PREGNANCY_TIME)));
    }

    public final boolean adult() {
        return this.age.getCurrent() >= ADULT;
    }

    protected void updateGauges() {
        health.minus(1);
        pregnancy.ifPresent(p -> {
            if (p.incAndWhelp(g -> {
                do {
                    birth(newWolf());
                    LOGGER.debug("A new wolf was born on {}", getPosition());
                } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
            })) {
                pregnancy = Optional.empty();
            }
        });
    }

    @Override
    protected void leaveScent() {
        // I don't smell
    }

    private Wolf newWolf() {
        return new Wolf(ai, getPosition(), 0, Sex.random(), getScentStorage());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "age=" + age +
            ", health=" + health +
            ", sex=" + sex +
            ", pregnant=" + pregnancy().map(Object::toString).orElse("none") +
            ", position=" + getPosition() +
            '}';
    }
}
