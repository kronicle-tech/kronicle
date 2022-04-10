package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class LogLevelStateTest {

    @Test
    public void constructorShouldMakeTopMessagesAnUnmodifiableList() {
        // Given
        LogLevelState underTest = LogLevelState.builder()
                .topMessages(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTopMessages().add(
                LogMessageState.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
