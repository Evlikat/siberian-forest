package net.evlikat.siberian.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RegularWolf extends Wolf {

    public RegularWolf(Position position, Sex sex) {
        super(position, sex);
    }

    @Override
    protected Optional<Food> feed(Visibility visibility) {
        if (this.wantsToEat()) {
            return visibility.units()
                    .map(unit -> unit instanceof Food ? (Food) unit : null)
                    .filter(Objects::nonNull)
                    .filter(unit -> this.canEat(unit.getClass()))
                    .findAny();
        }
        return Optional.empty();
    }

    private boolean wantsToEat() {
        return health.part() < 0.5d;
    }

    @Override
    protected Optional<Position> move(Visibility visibility) {
        List<Position> availableDirections = Arrays.stream(Direction.values())
                .map(dir -> getPosition().by(dir))
                .filter(newPos -> !newPos.adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                .collect(Collectors.toList());

        if (availableDirections.isEmpty()) {
            return Optional.empty();
        }

        if (this.wantsToEat()) {
            return Optional.of(visibility.units()
                    .map(InterestUnit::new)
                    .filter(p -> p.asFood != null)
                    .map(interest -> Pair.of(interest.asUnit.getPosition(), getPosition().distance(interest.asUnit.getPosition())))
                    .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                    .map(Pair::getKey)
                    .map(target -> getPosition().inDirectionTo(target))
                    .orElseGet(() ->
                            availableDirections.get(ThreadLocalRandom.current().nextInt(availableDirections.size() - 1))));
        } else {
            return Optional.of(visibility.units()
                    .map(InterestUnit::new)
                    .filter(p -> p.asMate != null && p.asMate.sex != this.sex)
                    .map(p -> p.asMate)
                    .map(mate -> Pair.of(mate.getPosition(), getPosition().distance(mate.getPosition())))
                    .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                    .map(Pair::getKey)
                    .map(target -> getPosition().inDirectionTo(target))
                    .orElseGet(() ->
                            availableDirections.get(ThreadLocalRandom.current().nextInt(availableDirections.size() - 1))));
        }
    }

    protected final RegularWolf newWolf() {
        return new RegularWolf(getPosition(), Sex.random());
    }

    private static class InterestUnit {

        final LivingUnit asUnit;
        final Food asFood;
        final Wolf asMate;

        InterestUnit(LivingUnit unit) {
            this.asUnit = unit;
            if (unit instanceof Food) {
                this.asFood = (Food) unit;
            } else {
                this.asFood = null;
            }
            if (unit instanceof Wolf) {
                this.asMate = (Wolf) unit;
            } else {
                this.asMate = null;
            }
        }
    }
}
