package net.evlikat.siberian.model;

public class UpdateResult {

    private final long elapsed;
    private final int rabbitsTotal;
    private final int wolvesTotal;

    public UpdateResult(long elapsed, int rabbitsTotal, int wolvesTotal) {

        this.elapsed = elapsed;
        this.rabbitsTotal = rabbitsTotal;
        this.wolvesTotal = wolvesTotal;
    }

    public long getElapsed() {
        return elapsed;
    }

    public int getRabbitsTotal() {
        return rabbitsTotal;
    }

    public int getWolvesTotal() {
        return wolvesTotal;
    }
}
