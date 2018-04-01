package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.utils.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class Rabbit extends LivingUnit<Rabbit> implements Food, RabbitInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rabbit.class);

    private static final Config CONF = Configuration.ROOT.getConfig("rabbit");

    public static final int MAX_AGE = CONF.getInt("maxAge");
    public static final int ADULT = CONF.getInt("adult");
    public static final int SPEED = CONF.getInt("speed");
    public static final int PREGNANCY_TIME = CONF.getInt("pregnancyTime");
    public static final int BIRTH_RATE = CONF.getInt("birthRate");
    public static final int FOOD_VALUE = CONF.getInt("foodValue");
    public static final int SIGHT = CONF.getInt("sight");

    protected final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();
    private final AI<RabbitInfo> ai;

    public Rabbit(AI<RabbitInfo> ai, Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(SIGHT, new NumberGauge(age, 0, MAX_AGE), SPEED, position, Collections.emptyList(), scentStorage);
        this.ai = ai;
        this.sex = sex;
    }

    @Override
    public Map<Position, Integer> evaluate(Visibility visibility) {
        return ai.evaluate(this, visibility);
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
    public Optional<Pregnancy> pregnancy() {
        return pregnancy;
    }

    @Override
    public Sex sex() {
        return sex;
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
    public void breed(Visibility visibility) {
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

    public boolean adult() {
        return age.getCurrent() >= ADULT;
    }

    private Rabbit newRabbit() {
        return new Rabbit(ai, getPosition(), 0, Sex.random(), getScentStorage());
    }

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
