package net.evlikat.siberian.model;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * RabbitExample
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class WolfExample {

    private static final Config WOLF_CONF =  Configuration.ROOT.getConfig("wolf");
    public static final int WOLF_MAX_AGE = WOLF_CONF.getInt("maxAge");

    private final int age;
    private final Sex sex;

    public WolfExample(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }

    public static WolfExample random() {
        return new WolfExample(randomAge(WOLF_MAX_AGE), Sex.random());
    }

    private static int randomAge(int max) {
        return ThreadLocalRandom.current().nextInt(max / 2);
    }
}
