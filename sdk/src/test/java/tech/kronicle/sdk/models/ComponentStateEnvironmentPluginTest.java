package tech.kronicle.sdk.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ComponentStateEnvironmentPluginTest {

    @Test
    public void constructorShouldMakeChecksAnUnmodifiableList() {
        // Given
        ComponentStateEnvironmentPlugin underTest = ComponentStateEnvironmentPlugin.builder()
                .checks(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getChecks().add(
                ComponentStateCheck.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeLogSummariesAnUnmodifiableList() {
        // Given
        ComponentStateEnvironmentPlugin underTest = ComponentStateEnvironmentPlugin.builder()
                .logSummaries(new ArrayList<>())
                .build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getLogSummaries().add(
                ComponentStateLogSummary.builder().build()
        ));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
