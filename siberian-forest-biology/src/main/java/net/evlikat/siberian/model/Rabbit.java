package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Rabbit extends LivingUnit implements Food {

    private static final Config CONF = ConfigFactory.load().getConfig("rabbit");

    private static final int MAX_AGE = CONF.getInt("maxAge");
    private static final double DIVISION_RATE = CONF.getDouble("division.rate");
    private static final int SIZE = CONF.getInt("draw.size");
    private static final int FOOD_VALUE = CONF.getInt("foodValue");
    private static final Color BORDER = new Color(117, 66, 16);

    public Rabbit(Position position) {
        super(3, MAX_AGE, position, Collections.emptyList());
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
    protected void multiply(Visibility visibility) {
        if (ThreadLocalRandom.current().nextDouble() < DIVISION_RATE) {
            birth(newRabbit());
        }
    }

    protected abstract Rabbit newRabbit();

    @Override
    protected void updateGauges() {
        health.minus(1);
    }
}
