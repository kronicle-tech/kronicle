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
import tech.kronicle.sdk.models.Dependency;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.datadog.dependencies.testutils.DependencyUtils.createDependency;

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
        Output<List<TracingData>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(List.of(), CACHE_TTL));
    }

    @Test
    public void findShouldReturnAndDependenciesTheClientFinds() {
        // Given
        List<Dependency> dependencies = List.of(
                createDependency(1),
                createDependency(2)
        );
        when(client.getDependencies("test-environment-1")).thenReturn(dependencies);
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1")), client);

        // When
        Output<List<TracingData>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                Output.ofOutput(
                        List.of(
                                TracingData.builder()
                                        .id("datadog-service-dependencies-test-environment-1")
                                        .name("Datadog Service Dependencies - test-environment-1")
                                        .pluginId("datadog")
                                        .environmentId("test-environment-1")
                                        .dependencies(dependencies)
                                        .build()
                        ),
                        CACHE_TTL
                )
        );
    }

    @Test
    public void findShouldFindDependenciesForAllEnvironments() {
        // Given
        List<Dependency> dependencies1 = List.of(
                createDependency(1),
                createDependency(2)
        );
        List<Dependency> dependencies2 = List.of(
                createDependency(3),
                createDependency(4)
        );
        when(client.getDependencies("test-environment-1")).thenReturn(dependencies1);
        when(client.getDependencies("test-environment-2")).thenReturn(dependencies2);
        underTest = new DatadogTracingDataFinder(new DatadogDependenciesConfig(List.of("test-environment-1", "test-environment-2")), client);

        // When
        Output<List<TracingData>, Void> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEqualTo(
                Output.ofOutput(
                        List.of(
                                TracingData.builder()
                                        .id("datadog-service-dependencies-test-environment-1")
                                        .name("Datadog Service Dependencies - test-environment-1")
                                        .pluginId("datadog")
                                        .environmentId("test-environment-1")
                                        .dependencies(dependencies1)
                                        .build(),
                                TracingData.builder()
                                        .id("datadog-service-dependencies-test-environment-2")
                                        .name("Datadog Service Dependencies - test-environment-2")
                                        .pluginId("datadog")
                                        .environmentId("test-environment-2")
                                        .dependencies(dependencies2)
                                        .build()
                        ),
                        CACHE_TTL
                )
        );
    }
}
