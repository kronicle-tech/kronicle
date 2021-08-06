package com.moneysupermarket.componentcatalog.sdk.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class MapUtilsTest {

    @Test
    public void createUnmodifiableMapShouldConvertMapToAnUnmodifiableMap() {
        // Given
        Map<String, String> modifiableMap = createModifiableMap();

        // When
        Map<String, String> returnValue = MapUtils.createUnmodifiableMap(modifiableMap);
        Throwable thrown = catchThrowable(() -> returnValue.put("test-key-3", "test-value-3"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void createUnmodifiableMapShouldConvertMapToAnUnmodifiableMapThatDoesNotReflectChangesToOriginalMap() {
        // Given
        Map<String, String> modifiableMap = createModifiableMap();

        // When
        Map<String, String> returnValue = MapUtils.createUnmodifiableMap(modifiableMap);
        modifiableMap.put("test-key-3", "test-value-3");

        // Then
        assertThat(returnValue).containsOnly(Map.entry("test-key-1", "test-value-1"), Map.entry("test-key-2", "test-value-2"));
    }

    private HashMap<String, String> createModifiableMap() {
        HashMap<String, String> modifiableMap = new HashMap<>();
        modifiableMap.put("test-key-1", "test-value-1");
        modifiableMap.put("test-key-2", "test-value-2");
        return modifiableMap;
    }
}
