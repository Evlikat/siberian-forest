package net.evlikat.siberian.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mergeMaps(BinaryOperator<V> valueMerge, Map<K, V>... maps) {
        HashMap<K, V> result = new HashMap<>();
        for (Map<K, V> map : maps) {
            map.forEach((key, value) -> result.merge(key, value, valueMerge));
        }
        return result;
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    public static <K> List<Map.Entry<K, Integer>> best(Map<K, Integer> all) {
        return all.entrySet().stream()
            .reduce(new ArrayList<>(),
                (l, e) -> {
                    if (l.isEmpty()) {
                        l.add(e);
                        return l;
                    }
                    if (l.get(0).getValue().equals(e.getValue())) {
                        l.add(e);
                    } else if (l.get(0).getValue() < e.getValue()) {
                        l.clear();
                        l.add(e);
                    }
                    return l;
                },
                (l1, l2) -> {
                    if (l1.isEmpty()) {
                        return l2;
                    }
                    if (l2.isEmpty()) {
                        return l1;
                    }
                    return l1.get(0).getValue() > l2.get(0).getValue() ? l1 : l2;
                }
            );
    }

}
