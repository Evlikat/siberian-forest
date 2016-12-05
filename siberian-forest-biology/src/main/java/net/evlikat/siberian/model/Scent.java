package net.evlikat.siberian.model;

import net.evlikat.siberian.model.stats.NumberGauge;

public class Scent {

    private final NumberGauge power = new NumberGauge(5, 0, 5);

    public void update() {
        power.minus(1);
    }

    public void restore() {
        power.setCurrent(power.getMax());
    }

    @Override
    public String toString() {
        return "Scent{" + power + '}';
    }
}
