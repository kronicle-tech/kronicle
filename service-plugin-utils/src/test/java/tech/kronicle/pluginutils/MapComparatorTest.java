package tech.kronicle.pluginutils;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginutils.MapComparator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MapComparatorTest {

    private final MapComparator<String, String> underTest = new MapComparator<>();

    @Test
    public void compareShouldReturnZeroWhenTheTwoMapsAreIdentical() {
        // Given
        Map<String, String> map1 = Map.ofEntries(
                Map.entry("key1", "value1"));
        Map<String, String> map2 = Map.ofEntries(
                Map.entry("key1", "value1"));

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void compareShouldReturn1WhenAKeyInMap2DoesNotExistInMap1() {
        // Given
        Map<String, String> map1 = Map.ofEntries();
        Map<String, String> map2 = Map.ofEntries(
                Map.entry("key1", "value1"));

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void compareShouldReturnMinus1WhenAKeyInMap1DoesNotExistInMap2() {
        // Given
        Map<String, String> map1 = Map.ofEntries(
                Map.entry("key1", "value1"));
        Map<String, String> map2 = Map.ofEntries();

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void compareShouldReturn1WhenAValueInMap1IsGreaterThanTheValueInMap2() {
        // Given
        Map<String, String> map1 = Map.ofEntries(
                Map.entry("key1", "b"));
        Map<String, String> map2 = Map.ofEntries(
                Map.entry("key1", "a"));

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void compareShouldReturnMinus1WhenAValueInMap1IsLessThanThanTheValueInMap2() {
        // Given
        Map<String, String> map1 = Map.ofEntries(
                Map.entry("key1", "a"));
        Map<String, String> map2 = Map.ofEntries(
                Map.entry("key1", "b"));

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void compareShouldCompareKeysInNaturalSortOrder() {
        // Given
        Map<String, String> map1 = Map.ofEntries(
                Map.entry("key1", "value1"),
                Map.entry("key2", "b"));
        Map<String, String> map2 = Map.ofEntries(
                Map.entry("key1", "value1"),
                Map.entry("key2", "a"));

        // When
        int returnValue = underTest.compare(map1, map2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }
}