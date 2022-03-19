package tech.kronicle.pluginapi.finders.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GenericTraceTest {

    @Test
    public void constructorShouldMakeSpansAnUnmodifiableList() {
        // Given
        GenericTrace underTest = GenericTrace.builder().spans(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getSpans().add(GenericSpan.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
