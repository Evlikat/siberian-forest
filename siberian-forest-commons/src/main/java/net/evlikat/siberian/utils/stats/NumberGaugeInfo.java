package net.evlikat.siberian.utils.stats;

public interface NumberGaugeInfo {

    float part();

    Integer getCurrent();

    Integer getMin();

    Integer getMax();

    boolean atMax();

    boolean atMin();
}
