package net.evlikat.siberian.model;

import net.evlikat.siberian.geo.Direction;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.utils.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.COMPETITOR;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.FOOD;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.MATE;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.PREDATOR;

public class RegularRabbitAI implements AI<RabbitInfo> {

    private static final Map<RegularRabbitTargetAttitude, Integer> VALUE_MAP
        = new EnumMap<>(RegularRabbitTargetAttitude.class);

    static {
        VALUE_MAP.put(RegularRabbitTargetAttitude.PREDATOR, -20);
        VALUE_MAP.put(RegularRabbitTargetAttitude.COMPETITOR, -5);
        VALUE_MAP.put(RegularRabbitTargetAttitude.MATE, 10);
        VALUE_MAP.put(RegularRabbitTargetAttitude.FOOD, 10);
    }

    private boolean wantsToEat(RabbitInfo me) {
        return me.health().part() < 0.5d;
    }

    @Override
    public Optional<Position> move(RabbitInfo me, Visibility visibility) {
        Map<Position, Set<RegularRabbitTargetAttitude>> positionValues = updateWithUnits(me, visibility.units());

        HashMap<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().stream().mapToInt(VALUE_MAP::get).sum(),
                        Integer::sum, HashMap::new));

        Map<Position, Integer> predatorValueMap = updateProliferatingValues(visibility, PREDATOR, positionValues);
        Map<Position, Integer> competitorValueMap = updateProliferatingValues(visibility, COMPETITOR, positionValues);

        Integer foodCellValue = VALUE_MAP.get(FOOD);
        Map<Position, Integer> cellValues = visibility.cells()
                .collect(Collectors.toMap(
                        Cell::getPosition,
                        c -> c.getGrass().getFoodCurrent() >= c.getGrass().getFoodValue() ? foodCellValue: 0));

        Map<Position, Integer> totalValueMap = CollectionUtils.mergeMaps(
                Integer::sum, predatorValueMap, competitorValueMap, positionValueMap, cellValues);
        return totalValueMap.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(max -> {
                    List<Direction> availableDirections = Direction.shuffledValues()
                            .filter(dir -> !me.getPosition().by(dir).adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                            .collect(Collectors.toList());
                    return me.getPosition().inDirectionTo(max.getKey(), availableDirections);
                });
    }

    private Map<Position, Integer> updateProliferatingValues(Visibility visibility, RegularRabbitTargetAttitude key,
                                                             Map<Position, Set<RegularRabbitTargetAttitude>> positionValues) {
        List<Position> negativePositions = positionValues.entrySet().stream()
                .filter(e -> e.getValue().contains(key))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Integer epicenterValue = VALUE_MAP.get(key);

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

    private Map<Position, Set<RegularRabbitTargetAttitude>> updateWithUnits(
        RabbitInfo me,
        Stream<? extends LivingUnitInfo> units
    ) {
        Map<Position, Set<RegularRabbitTargetAttitude>> positionValues = new HashMap<>();
        Map<Position, List<LivingUnitInfo>> positionUnits = units.collect(Collectors.groupingBy(LivingUnitInfo::getPosition));

        positionUnits.forEach((key, value) -> value.stream()
            .map(InterestUnit::new)
            .forEach(iu -> {
                if (iu.asPredator != null) {
                    positionValues.computeIfAbsent(key, (pos) -> new HashSet<>()).add(PREDATOR);
                }
                if (iu.asMate != null) {
                    RegularRabbitTargetAttitude attitude = goodPartner(me, iu.asMate) ? MATE : COMPETITOR;
                    positionValues.computeIfAbsent(key, (pos) -> new HashSet<>()).add(attitude);
                }
            }));

        return positionValues;
    }

    private boolean goodPartner(RabbitInfo me, Rabbit candidate) {
        return candidate.adult()
                && candidate.sex != me.sex()
                && !candidate.pregnancy().isPresent()
                && !me.pregnancy().isPresent();
    }

    @Override
    public Optional<Food> feed(RabbitInfo me, Visibility visibility) {
        if (!wantsToEat(me)) {
            return Optional.empty();
        }
        return visibility.cells()
                .filter(c -> c.getPosition().equals(me.getPosition()))
                .findAny()
                .map(Cell::getGrass);
    }

    private static class InterestUnit {

        final LivingUnitInfo asUnit;
        final Rabbit asMate;
        final Wolf asPredator;

        InterestUnit(LivingUnitInfo unit) {
            this.asUnit = unit;
            if (unit instanceof Rabbit) {
                this.asMate = (Rabbit) unit;
            } else {
                this.asMate = null;
            }
            if (unit instanceof Wolf) {
                this.asPredator = (Wolf) unit;
            } else {
                this.asPredator = null;
            }
        }
    }
}
