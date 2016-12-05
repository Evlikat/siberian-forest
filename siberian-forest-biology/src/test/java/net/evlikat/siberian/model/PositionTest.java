package net.evlikat.siberian.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.evlikat.siberian.model.Direction.*;
import static org.junit.Assert.assertEquals;

public class PositionTest {

    public static final List<Direction> ALL = Arrays.asList(values());

    @Test
    public void shouldProperlyCalculateNextPositionInDirectionToTarget() throws Exception {
        Position next = Position.on(5, 3).inDirectionTo(Position.on(2, 2), ALL);
        assertEquals(Position.on(5, 2), next);
    }

    @Test
    public void shouldReachTargetStepByStep() throws Exception {
        Position target = Position.on(2, 2);
        Position next = Position.on(5, 3).inDirectionTo(target, ALL);
        assertEquals(Position.on(5, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(Position.on(4, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(Position.on(3, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(Position.on(2, 2), next);
        next = next.inDirectionTo(target, ALL);
        assertEquals(Position.on(2, 2), next);
    }

    @Test
    public void shouldProperlyCalculateNextPositionAwayFromTarget() throws Exception {
        Position next = Position.on(5, 3).awayFrom(Position.on(2, 2), ALL);
        assertEquals(Position.on(6, 3), next);
    }

    @Test
    public void shouldProperlyCalculateNextPositionAwayFromTargetNearWall() throws Exception {
        Position next = Position.on(0, 3).awayFrom(Position.on(2, 3), Arrays.asList(EAST, NORTH, SOUTH));
        assertEquals(Position.on(0, 2), next);
    }
}