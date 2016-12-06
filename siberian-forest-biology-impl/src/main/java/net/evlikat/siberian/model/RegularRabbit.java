package net.evlikat.siberian.model;

import net.evlikat.siberian.utils.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.COMPETITOR;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.FOOD;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.MATE;
import static net.evlikat.siberian.model.RegularRabbitTargetAttitude.PREDATOR;

public class RegularRabbit extends Rabbit {

    public RegularRabbit(Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(position, age, sex, scentStorage);
    }

    @Override
    protected RegularRabbit newRabbit() {
        return new RegularRabbit(getPosition(), 0, Sex.random(), getScentStorage());
    }

    @Override
    public Optional<Position> move(Visibility visibility) {
        EnumMap<RegularRabbitTargetAttitude, Integer> valueMap = buildValueMap();
        Map<Position, Set<RegularRabbitTargetAttitude>> positionValues = updateWithUnits(visibility.units());

        HashMap<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().stream().mapToInt(valueMap::get).sum(),
                        Integer::sum, HashMap::new));

        List<Position> predatorPositions = positionValues.entrySet().stream()
                .filter(e -> e.getValue().contains(PREDATOR))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Integer predatorValue = valueMap.get(PREDATOR);

        Optional<Map<Position, Integer>> totalValueMap = predatorPositions.stream()
                .map(predatorPosition ->
                        (Map<Position, Integer>) visibility.cells()
                                .map(Cell::getPosition)
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        pos -> predatorValue + predatorPosition.distance(pos),
                                        Integer::sum, HashMap::new)))
                .reduce((map1, map2) -> CollectionUtils.mergeMaps(map1, map2, Integer::sum));

        return totalValueMap
                .map(tvm -> CollectionUtils.mergeMaps(tvm, positionValueMap, Integer::sum))
                .flatMap(
                        vm -> vm.entrySet().stream()
                                .max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue())))
                .map(max -> {
                    List<Direction> availableDirections = Direction.shuffledValues()
                            .filter(dir -> !getPosition().by(dir).adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                            .collect(Collectors.toList());
                    return getPosition().inDirectionTo(max.getKey(), availableDirections);
                });
    }

    private Map<Position, Set<RegularRabbitTargetAttitude>> updateWithUnits(Stream<LivingUnit> units) {
        Map<Position, Set<RegularRabbitTargetAttitude>> positionValues = new HashMap<>();
        Map<Position, List<LivingUnit>> positionUnits = units.collect(Collectors.groupingBy(LivingUnit::getPosition));

        positionUnits.entrySet()
                .forEach(p -> p.getValue().stream()
                        .map(InterestUnit::new)
                        .forEach(iu -> {
                            if (iu.asPredator != null) {
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(PREDATOR);
                            }
                            if (iu.asMate != null) {
                                RegularRabbitTargetAttitude attitude = COMPETITOR;
                                if (iu.asMate.adult() && iu.asMate.sex != this.sex) {
                                    attitude = MATE;
                                }
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(attitude);
                            }
                        })
                );

        return positionValues;
    }

    private EnumMap<RegularRabbitTargetAttitude, Integer> buildValueMap() {
        EnumMap<RegularRabbitTargetAttitude, Integer> valueMap = new EnumMap<>(RegularRabbitTargetAttitude.class);
        valueMap.put(RegularRabbitTargetAttitude.PREDATOR, -20);
        valueMap.put(RegularRabbitTargetAttitude.COMPETITOR, -5);
        valueMap.put(RegularRabbitTargetAttitude.MATE, this.wantsToEat() ? 0 : 10);
        return valueMap;
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

    private static class InterestUnit {

        final LivingUnit asUnit;
        final Rabbit asMate;
        final Wolf asPredator;

        InterestUnit(LivingUnit unit) {
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
