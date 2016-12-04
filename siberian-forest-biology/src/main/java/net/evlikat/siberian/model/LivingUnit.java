package net.evlikat.siberian.model;

import net.evlikat.siberian.model.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class LivingUnit implements DrawableUnit {

    private final static Logger LOGGER = LoggerFactory.getLogger(LivingUnit.class);

    private boolean alive = true;

    private final int sight;
    protected final NumberGauge age;
    protected final NumberGauge health = new NumberGauge(0, 100);
    private final List<Consumer<LivingUnit>> birthListeners = new LinkedList<>();
    private final List<Class<? extends Food>> canEat;

    private Position position;

    public LivingUnit(int sight, int maxAge, Position position, List<Class<? extends Food>> canEat) {
        this.sight = sight;
        this.age = new NumberGauge(0, 0, maxAge);
        this.position = position;
        this.canEat = canEat;
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

    @Override
    public final void update(Visibility visibility) {
        updateUnitState();

        move(visibility)
                .filter(p -> p.distance(getPosition()) <= 1)
                .ifPresent(p -> {
                    setPosition(p);
                    Visibility localVisibility = visibility.local(p);
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
                });
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

    public void birth(LivingUnit livingUnit) {
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

    public void addBirthListener(Consumer<LivingUnit> listener) {
        birthListeners.add(listener);
    }

    protected abstract void updateGauges();

    public final boolean canEat(Class<? extends Food> foodClass) {
        return canEat.stream().anyMatch(eatable -> eatable.isAssignableFrom(foodClass));
    }
}
