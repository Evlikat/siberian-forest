package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Direction;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.utils.CollectionUtils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.RegularWolfTargetAttitude.COMPETITOR;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.FOOD;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.MATE;

public class RegularWolf extends Wolf {

    private static final Map<RegularWolfTargetAttitude, Function<RegularWolf, Integer>> VALUE_MAP
            = new EnumMap<>(RegularWolfTargetAttitude.class);

    static {
        VALUE_MAP.put(RegularWolfTargetAttitude.COMPETITOR, w -> -5);
        VALUE_MAP.put(RegularWolfTargetAttitude.MATE, w -> w.wantsToMultiply() ? 15 : 0);
        VALUE_MAP.put(RegularWolfTargetAttitude.FOOD, w -> w.wantsToEat() ? 10 : 0);
    }

    RegularWolf(Position position, int age, Sex sex, ScentStorage scentStorage) {
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

    private boolean wantsToMultiply() {
        return !pregnancy().isPresent() && adult();
    }

    @Override
    protected Optional<Position> move(Visibility visibility) {
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues = updateWithUnits(visibility.units());
        Map<Position, Integer> cellValues = updateWithCells(visibility.cells());

        Map<Position, Integer> competitorValueMap = updateProliferatingValues(visibility, COMPETITOR, positionValues);

        Map<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .mapToInt(attr -> VALUE_MAP.get(attr).apply(this)).sum()));
        cellValues.entrySet().forEach(e -> positionValueMap.merge(e.getKey(), e.getValue(), Integer::sum));

        return CollectionUtils.mergeMaps(Integer::sum, positionValueMap, competitorValueMap).entrySet().stream()
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
                                RegularWolfTargetAttitude attitude = goodPartner(iu.asMate) ? MATE : COMPETITOR;
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(attitude);
                            }
                        })
                );

        return positionValues;
    }

    private Map<Position, Integer> updateProliferatingValues(Visibility visibility, RegularWolfTargetAttitude key,
                                                             Map<Position, Set<RegularWolfTargetAttitude>> positionValues) {
        List<Position> negativePositions = positionValues.entrySet().stream()
                .filter(e -> e.getValue().contains(key))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Integer epicenterValue = VALUE_MAP.get(key).apply(this);

        return negativePositions.stream()
                .map(position ->
                        (Map<Position, Integer>) visibility.cells()
                                .map(Cell::getPosition)
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        pos -> epicenterValue + position.distance(pos),
                                        Integer::sum, HashMap::new)))
                .reduce((map1, map2) -> CollectionUtils.mergeMaps(Integer::sum, map1, map2)).orElse(Collections.emptyMap());
    }

    private boolean goodPartner(Wolf candidate) {
        return candidate.adult()
                && candidate.sex != this.sex
                && !candidate.pregnancy().isPresent()
                && !pregnancy().isPresent();
    }

    private Map<Position, Integer> updateWithCells(Stream<Cell> cells) {
        return cells.collect(Collectors.toMap(Cell::getPosition, c -> c.getScent().get() / 2));
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
