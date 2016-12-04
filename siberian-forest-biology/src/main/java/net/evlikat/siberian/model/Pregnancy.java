package net.evlikat.siberian.model;

import net.evlikat.siberian.model.stats.NumberGauge;

import java.util.function.Consumer;

class Pregnancy {

    private final NumberGauge pregnancyCounter;

    public Pregnancy(Integer awaitTime) {
        this.pregnancyCounter = new NumberGauge(0, 0, awaitTime);
    }

    public boolean incAndWhelp(Consumer<NumberGauge> birth) {
        pregnancyCounter.inc();
        if (pregnancyCounter.atMax()) {
            birth.accept(pregnancyCounter);
            return true;
        }
        return false;
    }
}
