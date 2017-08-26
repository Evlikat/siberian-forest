package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Optional;

public class RemoteWolfAI implements AI<WolfInfo> {

    @Override
    public Optional<Position> move(WolfInfo unit, Visibility visibility) {
        return null;
    }

    @Override
    public Optional<Food> feed(WolfInfo unit, Visibility visibility) {
        return null;
    }
}
