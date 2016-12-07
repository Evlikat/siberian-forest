package net.evlikat.siberian.model;

public class UpdateResult {

    private final long elapsed;
    private final int unitsTotal;

    public UpdateResult(long elapsed, int unitsTotal) {

        this.elapsed = elapsed;
        this.unitsTotal = unitsTotal;
    }

    public long getElapsed() {
        return elapsed;
    }

    public int getUnitsTotal() {
        return unitsTotal;
    }
}
