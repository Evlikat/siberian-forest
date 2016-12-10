package net.evlikat.siberian.model.draw.factory;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.model.Grass;

public class GrassFactory {

    protected static final Config CONF = Configuration.ROOT.getConfig("grass");

    private static final int FOOD_VALUE = CONF.getInt("foodValue");
    private static final int MAX = CONF.getInt("max");
    private static final int INCREASE_RATE = CONF.getInt("increaseRate");

    public Grass createGrass() {
        return new Grass(FOOD_VALUE, MAX, INCREASE_RATE);
    }
}
