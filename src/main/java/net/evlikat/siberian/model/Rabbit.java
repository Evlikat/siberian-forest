package net.evlikat.siberian.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Rabbit extends LivingUnit implements DrawableUnit {

    public static final int SIZE = 10;
    public static final Color BORDER = Color.PINK;

    private final Field field;

    public Rabbit(Field field, Position position) {
        super(position);
        this.field = field;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(BORDER);
        g.fillRect(0, 0, SIZE - 1, SIZE - 1);
    }

    @Override
    public void update() {
        List<Position> availableDirections = Arrays.stream(Direction.values())
                .map(dir -> getPosition().by(dir))
                .filter(newPos -> !newPos.adjustableIn(0, 0, field.getWidth(), field.getHeight()))
                .collect(Collectors.toList());

        if (availableDirections.isEmpty()) {
            return;
        }
        Position newPosition = availableDirections
                .get(ThreadLocalRandom.current().nextInt(availableDirections.size()));
        setPosition(newPosition);
    }
}
