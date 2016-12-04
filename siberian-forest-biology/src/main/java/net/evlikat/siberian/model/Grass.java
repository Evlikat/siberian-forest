package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.stats.NumberGauge;

import java.awt.Color;
import java.awt.Graphics2D;

import static net.evlikat.siberian.utils.ColorUtils.modify;

public class Grass implements Food, DrawableUnit {

    protected static final Config CONF = ConfigFactory.load().getConfig("grass");

    private static final int FOOD_VALUE = CONF.getInt("foodValue");
    private static final int MAX = CONF.getInt("max");
    private static final int INCREASE_RATE = CONF.getInt("increaseRate");

    private static final Color COLOR = new Color(159, 235, 150);

    private final NumberGauge amount = new NumberGauge(0, MAX / 2, MAX);
    private final Position position;

    public Grass(Position position) {
        this.position = position;
    }

    @Override
    public int getFoodValue() {
        return FOOD_VALUE;
    }

    @Override
    public boolean eaten() {
        if (amount.getCurrent() >= getFoodValue()) {
            amount.minus(getFoodValue());
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(modify(COLOR, 1 - amount.part()));
        g.fillRect(0, 0, (int) g.getClipBounds().getWidth(), (int) g.getClipBounds().getHeight());
    }

    @Override
    public void update(Visibility visibility) {
        amount.plus(INCREASE_RATE);
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
