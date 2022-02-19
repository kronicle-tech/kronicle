package tech.kronicle.service.utils;

import java.util.Map;
import java.util.stream.Collector;

public final class MapCollectors {

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private MapCollectors() {
    }
}
