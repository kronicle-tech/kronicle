package tech.kronicle.sdk.utils;

import java.util.List;

import static java.util.Objects.nonNull;

public final class ListUtils {

    public static <T> List<T> createUnmodifiableList(List<T> list) {
        return nonNull(list) ? List.copyOf(list) : List.of();
    }

    private ListUtils() {
    }
}
