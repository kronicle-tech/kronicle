package tech.kronicle.sdk.utils;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

public final class ListUtils {

    public static <T> List<T> createUnmodifiableList(List<T> list) {
        return nonNull(list) ? List.copyOf(list) : List.of();
    }

    public static <T> List<T> unmodifiableUnionOfLists(List<List<T>> lists) {
        return lists.stream()
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private ListUtils() {
    }
}
