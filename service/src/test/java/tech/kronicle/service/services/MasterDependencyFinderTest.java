package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.service.finders.DependencyFinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MasterDependencyFinderTest {

    private MasterDependencyFinder underTest;
    @Mock
    private FinderRegistry finderRegistry;
    @Mock
    private DependencyFinder dependencyFinder1;
    @Mock
    private DependencyFinder dependencyFinder2;

    @Test
    public void getDependenciesShouldReturnAllDependenciesFromAllDependencyFinders() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        Dependency dependency1 = new Dependency("test-service-1", "test-service-2");
        Dependency dependency2 = new Dependency("test-service-3", "test-service-4");
        when(dependencyFinder1.find(componentMetadata)).thenReturn(List.of(dependency1, dependency2));
        Dependency dependency3 = new Dependency("test-service-5", "test-service-6");
        Dependency dependency4 = new Dependency("test-service-7", "test-service-8");
        when(dependencyFinder2.find(componentMetadata)).thenReturn(List.of(dependency3, dependency4));

        // When
        List<Dependency> returnValue = underTest.getDependencies(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(dependency1, dependency2, dependency3, dependency4);
    }

    @Test
    public void getDependenciesShouldDeduplicateDependencies() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        Dependency dependency1 = new Dependency("test-service-1", "test-service-2");
        Dependency dependency2 = new Dependency("test-service-3", "test-service-4");
        when(dependencyFinder1.find(componentMetadata)).thenReturn(List.of(dependency1, dependency2));
        Dependency dependency3 = new Dependency("test-service-5", "test-service-6");
        when(dependencyFinder2.find(componentMetadata)).thenReturn(List.of(dependency2, dependency3));

        // When
        List<Dependency> returnValue = underTest.getDependencies(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(dependency1, dependency2, dependency3);
    }
}
