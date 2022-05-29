package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class LogSummaryTest {

    @Test
    public void constructorShouldMakeLevelsAnUnmodifiableList() {
        // Given
        LogSummary underTest = LogSummary.builder()
                .levels(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLevels().add(
                LogLevelSummary.builder().build())
        );

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
