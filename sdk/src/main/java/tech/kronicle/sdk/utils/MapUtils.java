package tech.kronicle.sdk.utils;

import java.util.Map;

import static java.util.Objects.nonNull;

public final class MapUtils {

    public static <K, V> Map<K, V> createUnmodifiableMap(Map<K, V> map) {
        return nonNull(map) ? Map.copyOf(map) : Map.of();
    }

    private MapUtils() {
    }
}
