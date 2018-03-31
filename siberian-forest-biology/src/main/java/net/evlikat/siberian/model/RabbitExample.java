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
public class RabbitExample {

    private static final Config RABBIT_CONF =  Configuration.ROOT.getConfig("rabbit");
    public static final int RABBIT_MAX_AGE = RABBIT_CONF.getInt("maxAge");

    private final int age;
    private final Sex sex;

    public RabbitExample(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }

    public static RabbitExample random() {
        return new RabbitExample(randomAge(RABBIT_MAX_AGE), Sex.random());
    }

    private static int randomAge(int max) {
        return ThreadLocalRandom.current().nextInt(max / 2);
    }
}
