package tech.kronicle.plugins.aws.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentDataTest {

    @Test
    public void constructorShouldMakeLogGroupNamePatternsAnUnmodifiableList() {
        // Given
        ComponentData underTest = new ComponentData(new ArrayList<>(), null);

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLogGroupNamePatterns().add(""));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
