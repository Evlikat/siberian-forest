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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.RegularWolfTargetAttitude.COMPETITOR;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.FOOD;
import static net.evlikat.siberian.model.RegularWolfTargetAttitude.MATE;
import static net.evlikat.siberian.utils.CollectionUtils.best;

public class RegularWolfAI implements AI<WolfInfo> {

    private static final Map<RegularWolfTargetAttitude, BiFunction<RegularWolfAI, WolfInfo, Integer>> VALUE_MAP
        = new EnumMap<>(RegularWolfTargetAttitude.class);

    static {
        VALUE_MAP.put(RegularWolfTargetAttitude.COMPETITOR, (ai, me) -> -5);
        VALUE_MAP.put(RegularWolfTargetAttitude.MATE, (ai, me) -> ai.wantsToMultiply(me) ? 15 : 0);
        VALUE_MAP.put(RegularWolfTargetAttitude.FOOD, (ai, me) -> ai.wantsToEat(me) ? 10 : 0);
    }

    @Override
    public Optional<Food> feed(WolfInfo me, Visibility visibility) {
        if (wantsToEat(me)) {
            return visibility.units()
                .map(unit -> unit instanceof Food ? (Food) unit : null)
                .filter(Objects::nonNull)
                .filter(unit -> unit instanceof Rabbit)
                .findAny();
        }
        return Optional.empty();
    }

    private boolean wantsToEat(WolfInfo me) {
        return me.health().part() < 0.5d;
    }

    private boolean wantsToMultiply(WolfInfo me) {
        return !me.pregnancy().isPresent() && me.adult();
    }

    @Override
    public List<Position> aim(WolfInfo me, Visibility visibility) {
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues = updateWithUnits(me, visibility.units());
        Map<Position, Integer> cellValues = updateWithCells(visibility.cells());

        Map<Position, Integer> competitorValueMap = updateProliferatingValues(me, visibility, COMPETITOR, positionValues);

        Map<Position, Integer> positionValueMap = positionValues.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                .mapToInt(attr -> VALUE_MAP.get(attr).apply(this, me)).sum()));
        cellValues.forEach((key, value) -> positionValueMap.merge(key, value, Integer::sum));
        List<Map.Entry<Position, Integer>> res = best(CollectionUtils.mergeMaps(Integer::sum, positionValueMap, competitorValueMap));
        return res.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Optional<Position> move(WolfInfo me, Visibility visibility) {
        return aim(me, visibility).stream().findFirst()
            .map(bestPos -> {
                List<Direction> availableDirections = Direction.shuffledValues()
                    .filter(dir -> !me.getPosition().by(dir).adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                    .collect(Collectors.toList());
                return me.getPosition().inDirectionTo(bestPos, availableDirections);
            });
    }

    private Map<Position, Set<RegularWolfTargetAttitude>> updateWithUnits(WolfInfo me, Stream<? extends LivingUnitInfo> units) {
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues = new HashMap<>();
        Map<Position, List<LivingUnitInfo>> positionUnits = units.collect(Collectors.groupingBy(LivingUnitInfo::getPosition));

        positionUnits.forEach((key, value) -> value.stream()
            .map(InterestUnit::new)
            .forEach(iu -> {
                if (iu.asFood != null) {
                    positionValues.computeIfAbsent(key, (pos) -> new HashSet<>()).add(FOOD);
                }
                if (iu.asMate != null) {
                    RegularWolfTargetAttitude attitude = goodPartner(me, iu.asMate) ? MATE : COMPETITOR;
                    positionValues.computeIfAbsent(key, (pos) -> new HashSet<>()).add(attitude);
                }
            }));

        return positionValues;
    }

    private Map<Position, Integer> updateProliferatingValues(
        WolfInfo me,
        Visibility visibility,
        RegularWolfTargetAttitude key,
        Map<Position, Set<RegularWolfTargetAttitude>> positionValues
    ) {
        List<Position> negativePositions = positionValues.entrySet().stream()
            .filter(e -> e.getValue().contains(key))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        Integer epicenterValue = VALUE_MAP.get(key).apply(this, me);

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

    private boolean goodPartner(WolfInfo me, Wolf candidate) {
        return candidate.adult()
            && candidate.sex != me.sex()
            && !candidate.pregnancy().isPresent()
            && !me.pregnancy().isPresent();
    }

    private Map<Position, Integer> updateWithCells(Stream<Cell> cells) {
        return cells.collect(Collectors.toMap(Cell::getPosition, c -> c.getScent().get() / 2));
    }

    private static class InterestUnit {

        final LivingUnitInfo asUnit;
        final Food asFood;
        final Wolf asMate;

        InterestUnit(LivingUnitInfo unit) {
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
