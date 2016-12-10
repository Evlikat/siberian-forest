package net.evlikat.siberian.model;

import net.evlikat.siberian.utils.stats.NumberGauge;

public class Grass implements Food {

    private final NumberGauge amount;
    private final int foodValue;
    private final int increaseRate;

    public Grass(int foodValue, int max, int increaseRate) {
        this.foodValue = foodValue;
        this.increaseRate = increaseRate;
        this.amount = new NumberGauge(0, max / 2, max);
    }

    @Override
    public int getFoodValue() {
        return foodValue;
    }

    public int getFoodCurrent() {
        return amount.getCurrent();
    }

    @Override
    public boolean eaten() {
        if (amount.getCurrent() >= getFoodValue()) {
            amount.minus(getFoodValue());
            return true;
        }
        return false;
    }

    public float amountPart() {
        return amount.part();
    }

    public void update() {
        amount.plus(increaseRate);
    }

    @Override
    public String toString() {
        return "Grass{" +
                "amount=" + amount +
                '}';
    }
}
