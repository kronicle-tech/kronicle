package com.moneysupermarket.componentcatalog.service.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectReferenceTest {

    @Test
    public void noArgsConstructorShouldSetValueToNull() {
        // When
        ObjectReference<String> underTest = new ObjectReference<>();

        // Then
        assertThat(underTest.get()).isNull();
        assertThat(underTest.isEmpty()).isTrue();
        assertThat(underTest.isPresent()).isFalse();
    }

    @Test
    public void allArgsConstructorWithNullValueShouldSetValueToNull() {
        // When
        ObjectReference<String> underTest = new ObjectReference<>(null);

        // Then
        assertThat(underTest.get()).isNull();
        assertThat(underTest.isEmpty()).isTrue();
        assertThat(underTest.isPresent()).isFalse();
    }

    @Test
    public void allArgsConstructorWithNonNullValueShouldSetValue() {
        // When
        ObjectReference<String> underTest = new ObjectReference<>("test");

        // Then
        assertThat(underTest.get()).isEqualTo("test");
        assertThat(underTest.isEmpty()).isFalse();
        assertThat(underTest.isPresent()).isTrue();
    }

    @Test
    public void setWithNullValueShouldSetValueToNull() {
        // Given
        ObjectReference<String> underTest = new ObjectReference<>("test");

        // When
        underTest.set((String) null);

        // Then
        assertThat(underTest.get()).isNull();
        assertThat(underTest.isEmpty()).isTrue();
        assertThat(underTest.isPresent()).isFalse();
    }

    @Test
    public void setWithNonNullValueShouldSetValue() {
        // Given
        ObjectReference<String> underTest = new ObjectReference<>("test");

        // When
        underTest.set("test2");

        // Then
        assertThat(underTest.get()).isEqualTo("test2");
        assertThat(underTest.isEmpty()).isFalse();
        assertThat(underTest.isPresent()).isTrue();
    }

    @Test
    public void clearShouldSetValueToNull() {
        // Given
        ObjectReference<String> underTest = new ObjectReference<>("test");

        // When
        underTest.clear();

        // Then
        assertThat(underTest.get()).isNull();
        assertThat(underTest.isEmpty()).isTrue();
        assertThat(underTest.isPresent()).isFalse();
    }

    @Test
    public void setWithMapperShouldApplyMapperToValue() {
        // Given
        ObjectReference<String> underTest = new ObjectReference<>("test");

        // When
        underTest.set(value -> value + "-appended");

        // Then
        assertThat(underTest.get()).isEqualTo("test-appended");
        assertThat(underTest.isEmpty()).isFalse();
        assertThat(underTest.isPresent()).isTrue();
    }
}
