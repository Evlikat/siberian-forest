package net.evlikat.siberian.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mergeMaps(BinaryOperator<V> valueMerge, Map<K, V>... maps) {
        HashMap<K, V> result = new HashMap<>();
        for (Map<K, V> map : maps) {
            map.entrySet().forEach(e2 -> result.merge(e2.getKey(), e2.getValue(), valueMerge));
        }
        return result;
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
