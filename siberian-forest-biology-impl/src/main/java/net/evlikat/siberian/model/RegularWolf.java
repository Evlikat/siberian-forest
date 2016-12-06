package net.evlikat.siberian.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.RegularWolfTargetAttitude.COMPETITOR;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.FOOD;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.MATE;

public class RegularWolf extends Wolf {

    public RegularWolf(Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(position, age, sex, scentStorage);
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
        EnumMap<RegularWolfTargetAttitude, Integer> valueMap = buildValueMap();
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues = updateWithUnits(visibility.units());
        Map<Position, Integer> cellValues = updateWithCells(visibility.cells());

        Map<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().mapToInt(valueMap::get).sum()));
        cellValues.entrySet().forEach(e -> positionValueMap.merge(e.getKey(), e.getValue(), Integer::sum));

        return positionValueMap.entrySet().stream()
                .max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                .map(max -> {
                    List<Direction> availableDirections = Direction.shuffledValues()
                            .filter(dir -> !getPosition().by(dir).adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                            .collect(Collectors.toList());
                    return getPosition().inDirectionTo(max.getKey(), availableDirections);
                });
    }

    private Map<Position, Set<RegularWolfTargetAttitude>> updateWithUnits(Stream<LivingUnit> units) {
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues = new HashMap<>();
        Map<Position, List<LivingUnit>> positionUnits = units.collect(Collectors.groupingBy(LivingUnit::getPosition));

        positionUnits.entrySet()
                .forEach(p -> p.getValue().stream()
                        .map(InterestUnit::new)
                        .forEach(iu -> {
                            if (iu.asFood != null) {
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(FOOD);
                            }
                            if (iu.asMate != null) {
                                RegularWolfTargetAttitude attitude = COMPETITOR;
                                if (iu.asMate.adult() && iu.asMate.sex != this.sex) {
                                    attitude = MATE;
                                }
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(attitude);
                            }
                        })
                );

        return positionValues;
    }

    private Map<Position, Integer> updateWithCells(Stream<Cell> cells) {
        return cells.collect(Collectors.toMap(Cell::getPosition, c -> c.getScent().get() / 2));
    }

    private EnumMap<RegularWolfTargetAttitude, Integer> buildValueMap() {
        EnumMap<RegularWolfTargetAttitude, Integer> valueMap = new EnumMap<>(RegularWolfTargetAttitude.class);
        valueMap.put(RegularWolfTargetAttitude.COMPETITOR, -5);
        valueMap.put(RegularWolfTargetAttitude.FOOD, this.wantsToEat() ? 10 : 0);
        valueMap.put(RegularWolfTargetAttitude.MATE, this.wantsToEat() ? 0 : 10);
        return valueMap;
    }

    protected final RegularWolf newWolf() {
        return new RegularWolf(getPosition(), 0, Sex.random(), getScentStorage());
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
