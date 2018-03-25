package net.evlikat.siberian.geo;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DirectionTest {

    @Test
    public void shouldCalcOpposite() {
        assertEquals(Direction.EAST, Direction.WEST.opposite());
        assertEquals(Direction.WEST, Direction.EAST.opposite());
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite());
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite());
        assertEquals(Direction.NO, Direction.NO.opposite());
    }
}