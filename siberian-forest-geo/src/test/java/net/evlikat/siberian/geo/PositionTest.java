package net.evlikat.siberian.geo;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.evlikat.siberian.geo.Direction.EAST;
import static net.evlikat.siberian.geo.Direction.NORTH;
import static net.evlikat.siberian.geo.Direction.SOUTH;
import static net.evlikat.siberian.geo.Position.on;
import static org.junit.Assert.*;

public class PositionTest {

    public static final List<Direction> ALL = Arrays.asList(Direction.values());

    @Test
    public void shouldProperlyCalculateNextPositionInDirectionToTarget() throws Exception {
        Position next = on(5, 3).inDirectionTo(on(2, 2), ALL);
        assertEquals(on(5, 2), next);
    }

    @Test
    public void shouldReachTargetStepByStep() throws Exception {
        Position target = on(2, 2);
        Position next = on(5, 3).inDirectionTo(target, ALL);
        assertEquals(on(5, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(on(4, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(on(3, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(on(2, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(on(2, 2), next);
    }

    @Test
    public void shouldGetAllPositionsAround() throws Exception {
        Position center = on(2, 2);

        Set<Position> aroundPositions = center.around(2, new SimpleSized(100, 100));

        assertEquals(new HashSet<>(Arrays.asList(
                on(2, 0),
                on(1, 1), on(3, 1), on(2, 1),
                on(0, 2), on(1, 2), on(2, 2), on(3, 2), on(4, 2),
                on(1, 3), on(2, 3), on(3, 3),
                on(2, 4)
        )), aroundPositions);
    }

    @Test
    public void shouldProperlyCalculateNextPositionAwayFromTarget() throws Exception {
        Position next = on(5, 3).awayFrom(on(2, 2), ALL);
        assertEquals(on(6, 3), next);
    }

    @Test
    public void shouldProperlyCalculateNextPositionAwayFromTargetNearWall() throws Exception {
        Position next = on(0, 3).awayFrom(on(2, 3), Arrays.asList(EAST, NORTH, SOUTH));
        assertEquals(on(0, 2), next);
    }
}
