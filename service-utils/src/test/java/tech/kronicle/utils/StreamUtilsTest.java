package tech.kronicle.utils;

import lombok.Value;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamUtilsTest {

    @Test
    public void distinctByKeyShouldReturnTrueForAKeyItHasNotSeenBefore() {
        // Given
        TestItem item1 = new TestItem("test-key-1", "test-value-1");
        TestItem item2 = new TestItem("test-key-2", "test-value-2");
        Predicate<TestItem> underTest = StreamUtils.distinctByKey(TestItem::getKey);

        // When
        boolean returnValue = underTest.test(item1);

        // Then
        assertThat(returnValue).isTrue();

        // When
        returnValue = underTest.test(item2);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void distinctByKeyShouldReturnFalseForAKeyItHasSeenBefore() {
        // Given
        TestItem item1 = new TestItem("test-key-1", "test-value-1");
        TestItem item2 = new TestItem("test-key-1", "test-value-2");
        Predicate<TestItem> underTest = StreamUtils.distinctByKey(TestItem::getKey);

        // When
        boolean returnValue = underTest.test(item1);

        // Then
        assertThat(returnValue).isTrue();

        // When
        returnValue = underTest.test(item2);

        // Then
        assertThat(returnValue).isFalse();
    }

    @Test
    public void distinctByKeyShouldNotShareStateBetweenInstances() {
        // Given
        TestItem item = new TestItem("test-key-1", "test-value-1");
        Predicate<TestItem> underTest1 = StreamUtils.distinctByKey(TestItem::getKey);
        Predicate<TestItem> underTest2 = StreamUtils.distinctByKey(TestItem::getKey);

        // When
        boolean returnValue = underTest1.test(item);

        // Then
        assertThat(returnValue).isTrue();

        // When
        returnValue = underTest2.test(item);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Value
    private static class TestItem {

        String key;
        String value;
    }
}
