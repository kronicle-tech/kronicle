package tech.kronicle.utils;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Tag;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TagListComparatorTest {

    private final TagListComparator underTest = new TagListComparator();

    @Test
    public void compareShouldReturnZeroWhenTheTwoTagsAreIdentical() {
        // Given
        List<Tag> tags1 = List.of(
                new Tag("key1", "value1")
        );
        List<Tag> tags2 = List.of(
                new Tag("key1", "value1")
        );

        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void compareShouldReturn1WhenAKeyInTags2DoesNotExistInTags1() {
        // Given
        List<Tag> tags1 = List.of();
        List<Tag> tags2 = List.of(
                new Tag("key1", "value1")
        );

        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void compareShouldReturnMinus1WhenAKeyInTags1DoesNotExistInTags2() {
        // Given
        List<Tag> tags1 = List.of(
                new Tag("key1", "value1")
        );
        List<Tag> tags2 = List.of();

        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void compareShouldReturn1WhenAValueInTags1IsGreaterThanTheValueInTags2() {
        // Given
        List<Tag> tags1 = List.of(
                new Tag("key1", "b")
        );
        List<Tag> tags2 = List.of(
                new Tag("key1", "a")
        );
        
        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }

    @Test
    public void compareShouldReturnMinus1WhenAValueInTags1IsLessThanThanTheValueInTags2() {
        // Given
        List<Tag> tags1 = List.of(
                new Tag("key1", "a")
        );
        List<Tag> tags2 = List.of(
                new Tag("key1", "b")
        );
        
        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(-1);
    }

    @Test
    public void compareShouldCompareKeysInNaturalSortOrder() {
        // Given
        List<Tag> tags1 = List.of(
                new Tag("key1", "value1"),
                new Tag("key2", "b")
        );
        List<Tag> tags2 = List.of(
                new Tag("key1", "value1"),
                new Tag("key2", "a")
        );

        // When
        int returnValue = underTest.compare(tags1, tags2);

        // Then
        assertThat(returnValue).isEqualTo(1);
    }
}