package tech.kronicle.pluginutils.services;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapComparator<K extends Comparable<K>, V extends Comparable<V>> implements Comparator<Map<K, V>> {

    @Override
    public int compare(Map<K, V> o1, Map<K, V> o2) {
        Set<K> allKeys = new HashSet<>();
        allKeys.addAll(o1.keySet());
        allKeys.addAll(o2.keySet());
        List<K> sortedAllKeys = allKeys.stream().sorted().collect(Collectors.toList());

        for (K key : sortedAllKeys) {
            if (!o1.containsKey(key)) {
                return 1;
            }

            if (!o2.containsKey(key)) {
                return -1;
            }

            int result = o1.get(key).compareTo(o2.get(key));

            if (result != 0) {
                return result;
            }
        }

        return 0;
    }
}
