package tech.kronicle.utils;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MapCollectors {

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toUnmodifiableMap() {
        return Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private MapCollectors() {
    }
}
