package tech.kronicle.service.scanners.gradle.internal.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritingHashMapTest {

    public static final String TEST_KEY = "key";
    private static final String TEST_VALUE = "value";
    private static final String TEST_DIFFERENT_VALUE = "difference_value";
    private static final String TEST_DEFAULT = "default";

    @Test
    public void getShouldGetValueFromThisMapWhenAvailableInThisMapAndNoParent() {
        // Given
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>();
        underTest.put(TEST_KEY, TEST_VALUE);

        // When
        String returnValue = underTest.get(TEST_KEY);

        // Then
        assertThat(returnValue).isEqualTo(TEST_VALUE);
    }

    @Test
    public void getShouldNotGetValueFromParentWhenAvailableInThisMap() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        parent.put(TEST_KEY, TEST_DIFFERENT_VALUE);
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);
        underTest.put(TEST_KEY, TEST_VALUE);

        // When
        String returnValue = underTest.get(TEST_KEY);

        // Then
        assertThat(returnValue).isEqualTo(TEST_VALUE);
    }

    @Test
    public void getShouldGetValueFromParentWhenNotAvailableInThisMap() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        parent.put(TEST_KEY, TEST_DIFFERENT_VALUE);
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);

        // When
        String returnValue = underTest.get(TEST_KEY);

        // Then
        assertThat(returnValue).isEqualTo(TEST_DIFFERENT_VALUE);
    }

    @Test
    public void getShouldReturnNullWhenNotAvailableInThisMapAndParent() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);

        // When
        String returnValue = underTest.get(TEST_KEY);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getShouldReturnNullWhenNotAvailableInThisMapAndNoParent() {
        // Given
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>();

        // When
        String returnValue = underTest.get(TEST_KEY);

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void getOrDefaultShouldGetValueFromThisMapWhenAvailableInThisMapAndNoParent() {
        // Given
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>();
        underTest.put(TEST_KEY, TEST_VALUE);

        // When
        String returnValue = underTest.getOrDefault(TEST_KEY, TEST_DEFAULT);

        // Then
        assertThat(returnValue).isEqualTo(TEST_VALUE);
    }

    @Test
    public void getOrDefaultShouldNotGetValueFromParentWhenAvailableInThisMap() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        parent.put(TEST_KEY, TEST_DIFFERENT_VALUE);
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);
        underTest.put(TEST_KEY, TEST_VALUE);

        // When
        String returnValue = underTest.getOrDefault(TEST_KEY, TEST_DEFAULT);

        // Then
        assertThat(returnValue).isEqualTo(TEST_VALUE);
    }

    @Test
    public void getOrDefaultShouldGetValueFromParentWhenNotAvailableInThisMap() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        parent.put(TEST_KEY, TEST_DIFFERENT_VALUE);
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);

        // When
        String returnValue = underTest.getOrDefault(TEST_KEY, TEST_DEFAULT);

        // Then
        assertThat(returnValue).isEqualTo(TEST_DIFFERENT_VALUE);
    }

    @Test
    public void getOrDefaultShouldReturnDefaultWhenNotAvailableInThisMapAndParent() {
        // Given
        InheritingHashMap<String, String> parent = new InheritingHashMap<>();
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>(parent);

        // When
        String returnValue = underTest.getOrDefault(TEST_KEY, TEST_DEFAULT);

        // Then
        assertThat(returnValue).isEqualTo(TEST_DEFAULT);
    }

    @Test
    public void getOrDefaultShouldReturnDefaultWhenNotAvailableInThisMapAndNoParent() {
        // Given
        InheritingHashMap<String, String> underTest = new InheritingHashMap<>();

        // When
        String returnValue = underTest.getOrDefault(TEST_KEY, TEST_DEFAULT);

        // Then
        assertThat(returnValue).isEqualTo(TEST_DEFAULT);
    }
}
