package net.evlikat.siberian.model.stats;

public class NumberGauge extends Gauge<Integer> {

    public NumberGauge(Integer current, Integer min, Integer max) {
        super(current, min, max);
    }

    public NumberGauge(Integer min, Integer max) {
        super(max, min, max);
    }

    public float part() {
        return getCurrent().floatValue() / getMax().floatValue();
    }

    public void inc() {
        setCurrent(getCurrent() + 1);
    }

    public void dec() {
        setCurrent(getCurrent() - 1);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", getCurrent(), getMax());
    }
}
