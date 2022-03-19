package tech.kronicle.plugins.datadog.dependencies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.plugins.datadog.DatadogTracingDataFinder;
import tech.kronicle.plugins.datadog.dependencies.client.DatadogDependencyClient;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatadogTracingDataFinderTest {
    
    private DatadogTracingDataFinder underTest;
    @Mock
    private DatadogDependencyClient client;

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // Given
        underTest = new DatadogTracingDataFinder(null, null);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("datadog-tracing-data");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        underTest = new DatadogTracingDataFinder(null, null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches component dependencies from Datadog.  ");
    }

    @Test
    public void findShouldHandleEnvironmentsConfigBeingNull() {
        // Given
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(null), client);

        // When
        TracingData returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(TracingData.EMPTY);
    }

    @Test
    public void findShouldReturnAndDependenciesTheClientFinds() {
        // Given
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4")
        );
        when(client.getDependencies("test-environment-1")).thenReturn(dependencies);
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1")), client);

        // When
        TracingData returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                TracingData.builder()
                        .dependencies(dependencies)
                        .build()
        );
    }

    @Test
    public void findShouldFindDependenciesForAllEnvironments() {
        // Given
        when(client.getDependencies("test-environment-1")).thenReturn(List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4")
        ));
        when(client.getDependencies("test-environment-2")).thenReturn(List.of(
                new Dependency("test-service-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-8")
        ));
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1", "test-environment-2")), client);

        // When
        TracingData returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                TracingData.builder()
                        .dependencies(List.of(
                                new Dependency("test-service-1", "test-service-2"),
                                new Dependency("test-service-3", "test-service-4"),
                                new Dependency("test-service-5", "test-service-6"),
                                new Dependency("test-service-7", "test-service-8")
                        ))
                        .build()
        );
    }


    @Test
    public void findShouldDeduplicateTheDependencies() {
        // Given
        when(client.getDependencies("test-environment-1")).thenReturn(List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4")
        ));
        when(client.getDependencies("test-environment-2")).thenReturn(List.of(
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6")
        ));
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1", "test-environment-2")), client);

        // When
        TracingData returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                TracingData.builder()
                        .dependencies(List.of(
                                new Dependency("test-service-1", "test-service-2"),
                                new Dependency("test-service-3", "test-service-4"),
                                new Dependency("test-service-5", "test-service-6")
                        ))
                        .build()
        );
    }
}
