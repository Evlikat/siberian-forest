package net.evlikat.siberian.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Rabbit extends LivingUnit implements DrawableUnit {

    public static final double DIVISION_RATE = 0.1d;
    public static final int SIZE = 10;
    public static final Color BORDER = Color.PINK;

    public Rabbit(Position position) {
        super(3, position, Collections.emptyList());
    }

    @Override
    public void draw(Graphics2D g) {
        int xPadding = (int) (g.getClipBounds().getWidth() - SIZE) / 2;
        int yPadding = (int) (g.getClipBounds().getHeight() - SIZE) / 2;
        g.setColor(BORDER);
        g.fillRect(xPadding, yPadding, SIZE - 1, SIZE - 1);
    }

    @Override
    public void update(Visibility visibility) {
        update();

        List<Position> availableDirections = Arrays.stream(Direction.values())
                .map(dir -> getPosition().by(dir))
                .filter(newPos -> !newPos.adjustableIn(0, 0, visibility.getWidth(), visibility.getHeight()))
                .collect(Collectors.toList());

        if (availableDirections.isEmpty()) {
            return;
        }
        Position newPosition = availableDirections
                .get(ThreadLocalRandom.current().nextInt(availableDirections.size()));
        setPosition(newPosition);

        if (ThreadLocalRandom.current().nextDouble() < DIVISION_RATE) {
            birth(new Rabbit(getPosition()));
        }
    }

    @Override
    protected void updateGauges() {
        // immortal
    }
}
