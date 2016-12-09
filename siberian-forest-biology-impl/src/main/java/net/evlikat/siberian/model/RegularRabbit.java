package net.evlikat.siberian.model;

import net.evlikat.siberian.utils.CollectionUtils;

import java.util.Collections;
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

public class RegularRabbit extends Rabbit {

    private static final Map<RegularRabbitTargetAttitude, Integer> VALUE_MAP = new EnumMap<>(RegularRabbitTargetAttitude.class);

    static {
        VALUE_MAP.put(RegularRabbitTargetAttitude.PREDATOR, -20);
        VALUE_MAP.put(RegularRabbitTargetAttitude.COMPETITOR, -5);
        VALUE_MAP.put(RegularRabbitTargetAttitude.MATE, 10);
        VALUE_MAP.put(RegularRabbitTargetAttitude.FOOD, 10);
    }

    RegularRabbit(Position position, int age, Sex sex, ScentStorage scentStorage) {
        super(position, age, sex, scentStorage);
    }

    @Override
    protected RegularRabbit newRabbit() {
        return new RegularRabbit(getPosition(), 0, Sex.random(), getScentStorage());
    }

    @Override
    public Optional<Position> move(Visibility visibility) {
        Map<Position, Set<RegularRabbitTargetAttitude>> positionValues = updateWithUnits(visibility.units());

        HashMap<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, e -> e.getValue().stream().mapToInt(VALUE_MAP::get).sum(),
                        Integer::sum, HashMap::new));

        List<Position> predatorPositions = positionValues.entrySet().stream()
                .filter(e -> e.getValue().contains(PREDATOR))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Integer predatorValue = VALUE_MAP.get(PREDATOR);

        Map<Position, Integer> unitValueMap = predatorPositions.stream()
                .map(predatorPosition ->
                        (Map<Position, Integer>) visibility.cells()
                                .map(Cell::getPosition)
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        pos -> predatorValue + predatorPosition.distance(pos),
                                        Integer::sum, HashMap::new)))
                .reduce((map1, map2) -> CollectionUtils.mergeMaps(Integer::sum, map1, map2)).orElse(Collections.emptyMap());

        Integer foodCellValue = VALUE_MAP.get(FOOD);
        Map<Position, Integer> cellValues = visibility.cells()
                .collect(Collectors.toMap(
                        Cell::getPosition,
                        c -> c.getGrass().getFoodCurrent() >= c.getGrass().getFoodValue() ? foodCellValue: 0));

        Map<Position, Integer> totalValueMap = CollectionUtils.mergeMaps(
                Integer::sum, unitValueMap, positionValueMap, cellValues);
        return totalValueMap.entrySet().stream()
                .max((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
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
                                RegularRabbitTargetAttitude attitude = goodPartner(iu.asMate) ? MATE : COMPETITOR;
                                positionValues.computeIfAbsent(p.getKey(), (pos) -> new HashSet<>()).add(attitude);
                            }
                        })
                );

        return positionValues;
    }

    private boolean goodPartner(Rabbit candidate) {
        return candidate.adult()
                && candidate.sex != this.sex
                && !candidate.pregnancy().isPresent()
                && !pregnancy().isPresent();
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
