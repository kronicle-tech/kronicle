package tech.kronicle.utils;

import tech.kronicle.sdk.models.Tag;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.utils.MapCollectors.toUnmodifiableMap;


public class TagListComparator implements Comparator<List<Tag>> {

    @Override
    public int compare(List<Tag> o1, List<Tag> o2) {
        Map<String, String> map1 = getMap(o1);
        Map<String, String> map2 = getMap(o2);
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(map1.keySet());
        allKeys.addAll(map2.keySet());
        List<String> sortedAllKeys = allKeys.stream()
                .sorted()
                .collect(toUnmodifiableList());

        for (String key : sortedAllKeys) {
            String value1 = map1.get(key);
            String value2 = map2.get(key);

            if (isNull(value1)) {
                return 1;
            }

            if (isNull(value2)) {
                return -1;
            }

            int result = value1.compareTo(value2);

            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    private Map<String, String> getMap(List<Tag> tags) {
        return tags.stream()
                .map(tag -> Map.entry(tag.getKey(), tag.getValue()))
                .collect(toUnmodifiableMap());
    }
}
