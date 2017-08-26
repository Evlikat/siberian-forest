package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.geo.Position;

import java.util.concurrent.ThreadLocalRandom;

public class RemoteUnitAIZooFactory implements ZooFactory {

    private static final Config RABBIT_CONF =  Configuration.ROOT.getConfig("rabbit");
    private static final Config WOLF_CONF =  Configuration.ROOT.getConfig("wolf");

    public static final int RABBIT_MAX_AGE = RABBIT_CONF.getInt("maxAge");
    public static final int WOLF_MAX_AGE = WOLF_CONF.getInt("maxAge");

    @Override
    public Rabbit createRabbit(Position position, ScentStorage scentStorage) {
        return new Rabbit(new RemoteRabbitAI(), position, randomAge(RABBIT_MAX_AGE), Sex.random(), scentStorage);
    }

    @Override
    public Wolf createWolf(Position position, ScentStorage scentStorage) {
        return new Wolf(new RemoteWolfAI(), position, randomAge(WOLF_MAX_AGE), Sex.random(), scentStorage);
    }

    private int randomAge(int max) {
        return ThreadLocalRandom.current().nextInt(max / 2);
    }
}
