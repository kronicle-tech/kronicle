package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class EnvironmentStatePluginTest {

    @Test
    public void constructorShouldMakeChecksAnUnmodifiableList() {
        // Given
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .checks(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getChecks().add(
                CheckState.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeLogSummariesAnUnmodifiableList() {
        // Given
        EnvironmentPluginState underTest = EnvironmentPluginState.builder()
                .logSummaries(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLogSummaries().add(
                LogSummaryState.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
