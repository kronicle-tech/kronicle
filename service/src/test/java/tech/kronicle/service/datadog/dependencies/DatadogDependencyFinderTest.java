package tech.kronicle.service.datadog.dependencies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.datadog.dependencies.client.DatadogDependencyClient;
import tech.kronicle.service.datadog.dependencies.config.DatadogDependenciesConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatadogDependencyFinderTest {
    
    private DatadogDependencyFinder underTest;
    @Mock
    private DatadogDependencyClient client;

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // Given
        underTest = new DatadogDependencyFinder(null, null);

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("datadog-dependency");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // Given
        underTest = new DatadogDependencyFinder(null, null);

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Fetches component dependencies from Datadog.  ");
    }

    @Test
    public void findShouldHandleEnvironmentsConfigBeingNull() {
        // Given
        underTest = new DatadogDependencyFinder(new DatadogDependenciesConfig(null, null), client);

        // When
        List<Dependency> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldReturnAndDependenciesTheClientFinds() {
        // Given
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4")
        );
        when(client.getDependencies("test-environment-1")).thenReturn(dependencies);
        underTest = new DatadogDependencyFinder(new DatadogDependenciesConfig(null, List.of("test-environment-1")), client);

        // When
        List<Dependency> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).containsExactlyElementsOf(dependencies);
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
        underTest = new DatadogDependencyFinder(new DatadogDependenciesConfig(null, List.of("test-environment-1", "test-environment-2")), client);

        // When
        List<Dependency> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).containsExactlyElementsOf(List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-8")
        ));
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
        underTest = new DatadogDependencyFinder(new DatadogDependenciesConfig(null, List.of("test-environment-1", "test-environment-2")), client);

        // When
        List<Dependency> returnValue = underTest.find(null);

        // Then
        assertThat(returnValue).containsExactlyElementsOf(List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6")
        ));
    }
}
