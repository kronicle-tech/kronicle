package tech.kronicle.pluginapi.finders.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GenericSpanTest {

    @Test
    public void constructorShouldMakeSubComponentTagsAnUnmodifiableMap() {
        // Given
        GenericSpan underTest = GenericSpan.builder().subComponentTags(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSubComponentTags().add(GenericTag.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldSortSubComponentTagsByKeySoThatEqualityChecksAreNotAffectedByKeyOrder() {
        // Given
        GenericTag tagA = new GenericTag("a", "test-value-1");
        GenericTag tagB = new GenericTag("b", "test-value-1");
        GenericTag tagC = new GenericTag("c", "test-value-1");
        List<GenericTag> subComponentTags = List.of(
                tagC,
                tagA,
                tagB
        );
        GenericSpan underTest = GenericSpan.builder().subComponentTags(subComponentTags).build();

        // When
        List<GenericTag> returnValue = underTest.getSubComponentTags();

        // Then
        assertThat(returnValue).containsExactly(
                tagA,
                tagB,
                tagC
        );
    }
}
