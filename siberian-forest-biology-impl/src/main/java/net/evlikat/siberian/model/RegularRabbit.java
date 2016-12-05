package net.evlikat.siberian.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RegularRabbit extends Rabbit {

    public RegularRabbit(Position position) {
        super(position);
    }

    @Override
    protected RegularRabbit newRabbit() {
        return new RegularRabbit(getPosition());
    }

    @Override
    public Optional<Position> move(Visibility visibility) {
        List<Direction> availableDirections = Direction.shuffledValues()
                .filter(dir -> !getPosition().by(dir).adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                .collect(Collectors.toList());

        if (availableDirections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(visibility.units()
                .map(u -> u instanceof Wolf ? (Wolf) u : null)
                .filter(Objects::nonNull)
                .map(predator -> Pair.of(predator.getPosition(), getPosition().distance(predator.getPosition())))
                .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                .map(Pair::getKey)
                .map(target -> getPosition().awayFrom(target, availableDirections))
                .orElseGet(() -> chosenRandomly(availableDirections)));
    }

    private Position chosenRandomly(List<Direction> availableDirections) {
        return getPosition().adjust(availableDirections.get(
                ThreadLocalRandom.current().nextInt(availableDirections.size())));
    }

    @Override
    protected Optional<Food> feed(Visibility visibility) {
        if (!wantsToEat()) {
            return Optional.empty();
        }
        return visibility.cells()
                .filter(c -> c.getPosition().equals(getPosition()))
                .findAny()
                .map(Cell::getGrass);
    }

    private boolean wantsToEat() {
        return health.part() < 0.5d;
    }
}
