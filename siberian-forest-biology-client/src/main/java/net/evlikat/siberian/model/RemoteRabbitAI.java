package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Position;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class RemoteRabbitAI implements AI<RabbitInfo> {

    @Override
    public Map<Position, Integer> evaluate(RabbitInfo unit, Visibility visibility) {
        return Collections.emptyMap();
    }

    @Override
    public Optional<Position> move(RabbitInfo unit, Visibility visibility) {
        return Optional.empty();
    }

    @Override
    public Optional<Food> feed(RabbitInfo unit, Visibility visibility) {
        return Optional.empty();
    }
}
