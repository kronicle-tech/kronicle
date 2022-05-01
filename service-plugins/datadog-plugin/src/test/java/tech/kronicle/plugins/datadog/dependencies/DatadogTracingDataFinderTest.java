package tech.kronicle.plugins.datadog.dependencies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.datadog.DatadogTracingDataFinder;
import tech.kronicle.plugins.datadog.dependencies.client.DatadogDependencyClient;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Dependency;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatadogTracingDataFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

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
        Output<TracingData, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(TracingData.EMPTY, CACHE_TTL));
    }

    @Test
    public void findShouldReturnAndDependenciesTheClientFinds() {
        // Given
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"),
                createDependency("test-service-3", "test-service-4")
        );
        when(client.getDependencies("test-environment-1")).thenReturn(dependencies);
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1")), client);

        // When
        Output<TracingData, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                Output.ofOutput(
                        TracingData.builder()
                                .dependencies(dependencies)
                                .build(),
                        CACHE_TTL
                )
        );
    }

    @Test
    public void findShouldFindDependenciesForAllEnvironments() {
        // Given
        when(client.getDependencies("test-environment-1")).thenReturn(List.of(
                createDependency("test-service-1", "test-service-2"),
                createDependency("test-service-3", "test-service-4")
        ));
        when(client.getDependencies("test-environment-2")).thenReturn(List.of(
                createDependency("test-service-5", "test-service-6"),
                createDependency("test-service-7", "test-service-8")
        ));
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1", "test-environment-2")), client);

        // When
        Output<TracingData, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                Output.ofOutput(
                        TracingData.builder()
                                .dependencies(List.of(
                                        createDependency("test-service-1", "test-service-2"),
                                        createDependency("test-service-3", "test-service-4"),
                                        createDependency("test-service-5", "test-service-6"),
                                        createDependency("test-service-7", "test-service-8")
                                ))
                                .build(),
                        CACHE_TTL
                )
        );
    }

    @Test
    public void findShouldDeduplicateTheDependencies() {
        // Given
        when(client.getDependencies("test-environment-1")).thenReturn(List.of(
                createDependency("test-service-1", "test-service-2"),
                createDependency("test-service-3", "test-service-4")
        ));
        when(client.getDependencies("test-environment-2")).thenReturn(List.of(
                createDependency("test-service-3", "test-service-4"),
                createDependency("test-service-5", "test-service-6")
        ));
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1", "test-environment-2")), client);

        // When
        Output<TracingData, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                Output.ofOutput(
                        TracingData.builder()
                                .dependencies(List.of(
                                        createDependency("test-service-1", "test-service-2"),
                                        createDependency("test-service-3", "test-service-4"),
                                        createDependency("test-service-5", "test-service-6")
                                ))
                                .build(),
                        CACHE_TTL
                )
        );
    }

    private Dependency createDependency(String sourceComponentId, String targetComponentId) {
        return new Dependency(
                sourceComponentId,
                targetComponentId,
                DependencyTypeIds.TRACE,
                null,
                null
        );
    }
}
