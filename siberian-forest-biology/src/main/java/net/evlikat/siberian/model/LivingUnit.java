package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.draw.Drawable;
import net.evlikat.siberian.model.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class LivingUnit<T extends LivingUnit<T>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(LivingUnit.class);

    private boolean alive = true;

    private final int sight;
    private final int speed;
    private final ScentStorage scentStorage;
    protected final NumberGauge age;
    protected final NumberGauge health = new NumberGauge(0, 100);
    private final List<Consumer<T>> birthListeners = new LinkedList<>();
    private final List<Class<? extends Food>> canEat;

    private Position position;

    public LivingUnit(int sight, NumberGauge age, int speed,
                      Position position,
                      List<Class<? extends Food>> canEat,
                      ScentStorage scentStorage) {
        this.sight = sight;
        this.speed = speed;
        this.age = age;
        this.position = position;
        this.canEat = canEat;
        this.scentStorage = scentStorage;
    }

    public int getSight() {
        return sight;
    }

    public Position getPosition() {
        return position;
    }

    private void setPosition(Position position) {
        this.position = position;
    }

    public ScentStorage getScentStorage() {
        return scentStorage;
    }

    public final void update(Function<LivingUnit, Visibility> getVisibility) {
        updateUnitState();

        IntStream.range(0, speed)
                .forEach(step -> {
                    Visibility visibility = getVisibility.apply(this);
                    move(visibility)
                            .filter(p -> p.distance(getPosition()) <= speed)
                            .ifPresent(p -> {
                                if (p.in(visibility)) {
                                    setPosition(p);
                                    leaveScent();
                                }
                            });
                });
        Visibility localVisibility = getVisibility.apply(this).local(getPosition());
        Optional<Food> fed = feed(localVisibility);
        fed.ifPresent(d -> {
            if (d.eaten()) {
                LOGGER.debug("A {}[{}] on {} is eating", getClass().getSimpleName(), health, getPosition());
                health.setCurrent(health.getCurrent() + d.getFoodValue());
            }
        });
        if (health.part() > 0.5d) {
            multiply(localVisibility);
        }
    }

    protected void leaveScent() {
        scentStorage.update(getPosition());
    }

    protected abstract Optional<Position> move(Visibility visibility);

    protected abstract Optional<Food> feed(Visibility visibility);

    protected abstract void multiply(Visibility visibility);

    private void updateUnitState() {
        if (!alive) {
            return;
        }
        age.plus(1);
        updateGauges();
        if (dead()) {
            kill();
        }
    }

    private boolean dead() {
        return health.atMin() || age.atMax();
    }

    public boolean isAlive() {
        return alive;
    }

    public void birth(T livingUnit) {
        birthListeners.forEach(bl -> bl.accept(livingUnit));
    }

    public boolean kill() {
        if (alive) {
            LOGGER.debug("A {} died on {}", getClass().getSimpleName(), getPosition());
            alive = false;
            birthListeners.clear();
            return true;
        }
        return false;
    }

    public void addBirthListener(Consumer<T> listener) {
        birthListeners.add(listener);
    }

    protected abstract void updateGauges();

    public final boolean canEat(Class<? extends Food> foodClass) {
        return canEat.stream().anyMatch(eatable -> eatable.isAssignableFrom(foodClass));
    }

    public NumberGauge health() {
        return health;
    }
}
