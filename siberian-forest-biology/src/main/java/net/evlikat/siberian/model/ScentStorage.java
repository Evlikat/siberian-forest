package net.evlikat.siberian.model;

public interface ScentStorage {

    Scent get(Position position);

    void update(Position position);
}
