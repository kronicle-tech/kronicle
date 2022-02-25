package tech.kronicle.plugins.zipkin.models.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SpanTest {

    @Test
    public void constructorShouldMakeAnnotationsAnUnmodifiableList() {
        // Given
        Span underTest = Span.builder().annotations(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getAnnotations().add(Annotation.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTagsAnUnmodifiableMap() {
        // Given
        Span underTest = Span.builder().tags(new HashMap<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTags().put("test", "test"));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
