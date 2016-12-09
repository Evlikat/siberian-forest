package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.ThreadLocalRandom;

public class RegularZooFactory implements ZooFactory {

    private static final Config RABBIT_CONF = ConfigFactory.load().getConfig("rabbit");
    private static final Config WOLF_CONF = ConfigFactory.load().getConfig("wolf");

    public static final int RABBIT_MAX_AGE = RABBIT_CONF.getInt("maxAge");
    public static final int WOLF_MAX_AGE = WOLF_CONF.getInt("maxAge");

    @Override
    public Rabbit createRabbit(Position position, ScentStorage scentStorage) {
        return new RegularRabbit(position, randomAge(RABBIT_MAX_AGE), Sex.random(), scentStorage);
    }

    @Override
    public Wolf createWolf(Position position, ScentStorage scentStorage) {
        return new RegularWolf(position, randomAge(WOLF_MAX_AGE), Sex.random(), scentStorage);
    }

    private int randomAge(int max) {
        return ThreadLocalRandom.current().nextInt(max / 2);
    }
}
