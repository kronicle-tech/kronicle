package tech.kronicle.pluginapi.finders.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.Area;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TracingDataTest {

    @Test
    public void constructorShouldMakeDependenciesAnUnmodifiableList() {
        // Given
        TracingData underTest = TracingData.builder().dependencies(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getDependencies().add(Dependency.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void constructorShouldMakeTracesAnUnmodifiableList() {
        // Given
        TracingData underTest = TracingData.builder().traces(new ArrayList<>()).build();

        // When
        Throwable thrown = catchThrowable(() -> underTest.getTraces().add(GenericTrace.builder().build()));

        // Then
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }
}
