package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Wolf extends LivingUnit implements DrawableUnit {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wolf.class);

    protected static final Config CONF = ConfigFactory.load().getConfig("wolf");

    protected static final int SIGHT = CONF.getInt("sight");
    protected static final double BIRTH_RATE = CONF.getDouble("birthRate");
    protected static final int ADULT = CONF.getInt("adult");
    protected static final int PREGNANCY_TIME = CONF.getInt("pregnancyTime");
    protected static final int FETUS_SIZE = CONF.getInt("draw.fetus.size");
    protected static final int SIZE = CONF.getInt("draw.size");

    private static final Map<Sex, Color> SEX_COLOR = new EnumMap<>(Sex.class);
    private static final Color LOST_HEALTH = Color.RED;

    static {
        SEX_COLOR.put(Sex.MALE, Color.ORANGE);
        SEX_COLOR.put(Sex.FEMALE, Color.BLUE);
    }

    protected final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();

    public Wolf(Position position, Sex sex) {
        super(SIGHT, position, Collections.singletonList(Rabbit.class));
        this.sex = sex;
    }

    protected Optional<Pregnancy> pregnancy() {
        return pregnancy;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(LOST_HEALTH);
        g.fillOval(0, 0, SIZE - 1, SIZE - 1);
        g.setColor(SEX_COLOR.get(sex));
        g.fillArc(0, 0, SIZE - 1, SIZE - 1, 90, (int) (health.part() * 360));
        pregnancy.ifPresent(gauge -> g.fillRect(SIZE - FETUS_SIZE, SIZE, FETUS_SIZE - 1, FETUS_SIZE - 1));
    }

    @Override
    protected void multiply(Visibility visibility) {
        if (this.sex != Sex.FEMALE || this.age.getCurrent() < ADULT || pregnancy().isPresent()) {
            return;
        }
        visibility.units()
                .map(unit -> unit instanceof Wolf ? (Wolf) unit : null)
                .filter(Objects::nonNull)
                .filter(otherWolf -> otherWolf.sex != this.sex && otherWolf.age.getCurrent() > ADULT)
                .findAny()
                .ifPresent(mate -> pregnancy = Optional.of(new Pregnancy(PREGNANCY_TIME)));
    }

    protected void updateGauges() {
        health.dec();
        pregnancy.ifPresent(p -> {
            if (p.incAndWhelp(g -> {
                do {
                    birth(newWolf());
                    LOGGER.debug("New wolf was born on {}", getPosition());
                } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
            })) {
                pregnancy = Optional.empty();
            }
        });
    }

    protected abstract Wolf newWolf();
}
