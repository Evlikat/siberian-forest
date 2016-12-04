package net.evlikat.siberian.model;

import java.util.Arrays;
import java.util.List;
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

        List<Position> availableDirections = Arrays.stream(Direction.values())
                .map(dir -> getPosition().by(dir))
                .filter(newPos -> !newPos.adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                .collect(Collectors.toList());

        return Optional.of(availableDirections)
                .filter(c -> !c.isEmpty())
                .map(dirs -> dirs
                        .get(ThreadLocalRandom.current().nextInt(availableDirections.size())));
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
