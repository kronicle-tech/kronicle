package tech.kronicle.sdk.utils;

import tech.kronicle.sdk.models.ObjectWithId;
import tech.kronicle.sdk.models.ObjectWithIdAndMerge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class ObjectWithIdListUtils {

    public static <T extends ObjectWithIdAndMerge<T>> List<T> mergeObjectWithIdLists(
            List<T> listA,
            List<T> listB
    ) {
        Map<String, T> mapA = createObjectWithIdMap(listA);
        Map<String, T> mapB = createObjectWithIdMap(listB);
        List<T> newList = new ArrayList<>();
        getIds(listA).forEach(id -> {
            T a = mapA.get(id);
            T b = mapB.get(id);
            if (nonNull(b)) {
                newList.add(a.merge(b));
            } else {
                newList.add(a);
            }
        });
        getIds(listB).forEach(id -> {
            T a = mapA.get(id);
            T b = mapB.get(id);
            if (isNull(a)) {
                newList.add(b);
            }
        });
        return List.copyOf(newList);
    }

    private static <T extends ObjectWithId> Map<String, T> createObjectWithIdMap(List<T> list) {
        return list.stream()
                .collect(toUnmodifiableMap(T::getId, Function.identity()));
    }

    private static <T extends ObjectWithIdAndMerge<T>> List<String> getIds(List<T> list) {
        return list.stream()
                .map(T::getId)
                .collect(toUnmodifiableList());
    }

    private ObjectWithIdListUtils() {
    }
}
