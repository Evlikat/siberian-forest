package net.evlikat.siberian.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

import static java.util.Optional.ofNullable;

public final class Configuration {

    private Configuration() {
    }

    public static final Config ROOT = loadRoot();

    private static Config loadRoot() {
        return ofNullable(System.getProperty("config.type")).map(
                type -> {
                    switch (type) {
                        case "file":
                            return ConfigFactory.parseFile(
                                    new File(
                                            ofNullable(System.getProperty("config.location")).orElse("application.conf")
                                    )
                            );
                        case "resource":
                            return ConfigFactory.parseResources(
                                    ofNullable(System.getProperty("config.location")).orElse("application.conf")
                            );
                        default:
                            return null;
                    }
                }
        ).map(cnf -> cnf.withFallback(ConfigFactory.parseResources("application.conf"))).orElseGet(ConfigFactory::load);
    }
}
