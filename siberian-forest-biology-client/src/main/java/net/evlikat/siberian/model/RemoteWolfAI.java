package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RemoteWolfAI implements AI<WolfInfo> {
    @Override
    public List<Position> aim(WolfInfo unit, Visibility visibility) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Position> move(WolfInfo unit, Visibility visibility) {
        return Optional.empty();
    }

    @Override
    public Optional<Food> feed(WolfInfo unit, Visibility visibility) {
        return Optional.empty();
    }
}
