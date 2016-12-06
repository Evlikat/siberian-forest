package net.evlikat.siberian.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <K, V> Map<K, V> mergeMaps(Map<K, V> map1, Map<K, V> map2, BinaryOperator<V> valueMerge) {
        HashMap<K, V> result = new HashMap<>(map1);
        map2.entrySet().forEach(e2 -> result.merge(e2.getKey(), e2.getValue(), valueMerge));
        return result;
    }
}
