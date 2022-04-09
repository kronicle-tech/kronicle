package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentStateLogLevelTest {

    @Test
    public void constructorShouldMakeTopMessagesAnUnmodifiableList() {
        // Given
        ComponentStateLogLevel underTest = ComponentStateLogLevel.builder()
                .topMessages(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTopMessages().add(
                ComponentStateLogMessage.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
