package net.evlikat.siberian.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractCellTest {

    private static final class TestCell extends AbstractCell<TestCell> {

        public TestCell(Position position) {
            super(position);
        }
    }

    private Set<TestCell> generateNeighboursFor(Position position) {
        return Arrays.stream(Direction.values())
                .filter(d -> d != Direction.NO)
                .map(position::adjust)
                .map(TestCell::new)
                .collect(Collectors.toSet());
    }

    @Test
    public void shouldReturnEmptyListWhenHasNoNeighbours() {
        TestCell sample = new TestCell(Position.on(1, 1));

        Set<TestCell> neighbours = sample.neighbours(10);

        assertTrue(neighbours.isEmpty());
    }

    @Test
    public void shouldReturnNeighboursInRadius1() {
        TestCell sample = new TestCell(Position.on(1, 1));
        Set<TestCell> expectedNeighbours = generateNeighboursFor(Position.on(1, 1));
        expectedNeighbours.forEach(sample::addNeighbour);

        Set<TestCell> neighbours = sample.neighbours(1);

        assertEquals(expectedNeighbours, neighbours);
    }

    @Test
    public void shouldReturnNeighboursInRadius2() {
        TestCell sample = new TestCell(Position.on(1, 1));
        TestCell n0_1 = new TestCell(Position.on(0, 1));
        TestCell n1_0 = new TestCell(Position.on(1, 0));
        TestCell n2_1 = new TestCell(Position.on(2, 1));
        TestCell n1_2 = new TestCell(Position.on(1, 2));

        TestCell n2_2 = new TestCell(Position.on(2, 2));
        TestCell n2_0 = new TestCell(Position.on(2, 0));
        TestCell n3_1 = new TestCell(Position.on(3, 1));

        sample.addNeighbour(n0_1);
        sample.addNeighbour(n1_0);
        sample.addNeighbour(n2_1);
        sample.addNeighbour(n1_2);

        n2_1.addNeighbour(sample);
        n2_1.addNeighbour(n2_2);
        n2_1.addNeighbour(n2_0);
        n2_1.addNeighbour(n3_1);

        Set<TestCell> neighbours = sample.neighbours(2);

        assertEquals(new HashSet<>(Arrays.asList(
                n0_1,
                n1_0,
                n2_1,
                n1_2,
                n2_2,
                n2_0,
                n3_1
        )), neighbours);
    }
}