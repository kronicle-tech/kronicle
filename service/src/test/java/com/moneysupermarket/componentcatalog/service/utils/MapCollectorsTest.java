package com.moneysupermarket.componentcatalog.service.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MapCollectorsTest {

    @Test
    public void toMapShouldTransformStreamOfMapEntriesIntoMap() {
        // Given
        Stream<Map.Entry<String, String>> stream = Stream.of(Map.entry("key1", "value1"), Map.entry("key2", "value2"));

        // When
        Map<String, String> map = stream.collect(MapCollectors.toMap());

        // Then
        assertThat(map).hasSize(2);
        assertThat(map).containsEntry("key1", "value1");
        assertThat(map).containsEntry("key2", "value2");
    }
}
