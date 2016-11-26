package net.evlikat.siberian.model;

import net.evlikat.siberian.model.stats.NumberGauge;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class LivingUnit implements DrawableUnit {

    private final int sight;
    protected final NumberGauge health = new NumberGauge(0, 100);
    private final List<Consumer<LivingUnit>> birthListeners = new LinkedList<>();
    private final List<Consumer<LivingUnit>> deathListeners = new LinkedList<>();
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
        updateGauges();
        if (Objects.equals(health.getCurrent(), health.getMin())) {
            kill();
        }
    }

    public void birth(LivingUnit livingUnit) {
        birthListeners.forEach(bl -> bl.accept(livingUnit));
    }

    public void kill() {
        deathListeners.forEach(listener -> listener.accept(this));
        deathListeners.clear();
    }

    public void addDeathListener(Consumer<LivingUnit> listener) {
        deathListeners.add(listener);
    }

    public void addBirthListener(Consumer<LivingUnit> listener) {
        birthListeners.add(listener);
    }

    protected abstract void updateGauges();

    public boolean canEat(Class<? extends LivingUnit> eatable) {
        return canEat.contains(eatable);
    }
}
