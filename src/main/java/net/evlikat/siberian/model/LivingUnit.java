package net.evlikat.siberian.model;

import net.evlikat.siberian.model.stats.NumberGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class LivingUnit implements DrawableUnit {

    private final static Logger LOGGER = LoggerFactory.getLogger(LivingUnit.class);

    private boolean alive = true;

    private final int sight;
    protected final NumberGauge health = new NumberGauge(0, 100);
    protected final NumberGauge age = new NumberGauge(0, 0, 1000);
    private final List<Consumer<LivingUnit>> birthListeners = new LinkedList<>();
    private final List<Class<? extends LivingUnit>> canEat;

    private Position position;

    public LivingUnit(int sight, Position position, List<Class<? extends LivingUnit>> canEat) {
        this.sight = sight;
        this.position = position;
        this.canEat = canEat;
    }

    public Position getPosition() {
        return position;
    }

    protected void setPosition(Position position) {
        this.position = position;
    }

    public final void update() {
        if (!alive) {
            return;
        }
        age.inc();
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

    public boolean canEat(Class<? extends LivingUnit> eatable) {
        return canEat.contains(eatable);
    }
}
