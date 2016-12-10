package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

public interface ScentStorage {

    Scent get(Position position);

    void update(Position position);
}
