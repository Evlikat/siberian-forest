package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Rabbit extends LivingUnit implements Food {

    private static final Config CONF = ConfigFactory.load().getConfig("rabbit");

    private static final double DIVISION_RATE = CONF.getDouble("division.rate");
    private static final int SIZE = CONF.getInt("draw.size");
    public static final int FOOD_VALUE = CONF.getInt("foodValue");
    private static final Color BORDER = Color.PINK;

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
    public int getFoodValue() {
        return FOOD_VALUE;
    }

    @Override
    public boolean eaten() {
        return kill();
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
        // eat the solar power
        return Optional.empty();
    }

    @Override
    protected void multiply(Visibility visibility) {
        if (ThreadLocalRandom.current().nextDouble() < DIVISION_RATE) {
            birth(new Rabbit(getPosition()));
        }
    }

    @Override
    protected void updateGauges() {
        // immortal
    }
}
