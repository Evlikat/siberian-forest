package net.evlikat.siberian.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionTest {
    @Test
    public void shouldProperlyCalculateNextPositionInDirectionToTarget() throws Exception {
        Position next = Position.on(5, 3).inDirectionTo(Position.on(2, 2));
        assertEquals(Position.on(4, 3), next);
    }

    @Test
    public void shouldReachTargetStepByStep() throws Exception {
        Position target = Position.on(2, 2);
        Position next = Position.on(5, 3).inDirectionTo(target);
        assertEquals(Position.on(4, 3), next);
        next = next.inDirectionTo(target);
        assertEquals(Position.on(3, 3), next);
        next = next.inDirectionTo(target);
        assertEquals(Position.on(2, 3), next);
        next = next.inDirectionTo(target);
        assertEquals(Position.on(2, 2), next);
        next = next.inDirectionTo(target);
        assertEquals(Position.on(2, 2), next);
    }
}