package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.finders.DependencyFinder;
import tech.kronicle.plugintestutils.testutils.LogCaptor;
import tech.kronicle.plugintestutils.testutils.SimplifiedLogEvent;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MasterDependencyFinderTest {

    private MasterDependencyFinder underTest;
    @Mock
    private FinderExtensionRegistry finderRegistry;
    @Mock
    private ComponentAliasResolver componentAliasResolver;
    @Mock
    private DependencyFinder dependencyFinder1;
    @Mock
    private DependencyFinder dependencyFinder2;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(MasterDependencyFinder.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void getDependenciesShouldReturnAllDependenciesFromAllDependencyFinders() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry, componentAliasResolver);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(Map.of());
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
        underTest = new MasterDependencyFinder(finderRegistry, componentAliasResolver);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(Map.of());
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

    @Test
    public void getDependenciesShouldMapComponentAliasIds() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry, componentAliasResolver);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(
                Map.ofEntries(
                        Map.entry("test-service-alias-1", "test-service-1"),
                        Map.entry("test-service-alias-4", "test-service-4"),
                        Map.entry("test-service-alias-5", "test-service-5"),
                        Map.entry("test-service-alias-8", "test-service-8")
                )
        );
        when(dependencyFinder1.find(componentMetadata)).thenReturn(List.of(
                new Dependency("test-service-alias-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-alias-4")
        ));
        when(dependencyFinder2.find(componentMetadata)).thenReturn(List.of(
                new Dependency("test-service-alias-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-alias-8")
        ));

        // When
        List<Dependency> returnValue = underTest.getDependencies(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-8")
        );
    }

    @Test
    public void getDependenciesShouldDeduplicateMappedComponentAliasIds() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry, componentAliasResolver);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(
                Map.ofEntries(
                        Map.entry("test-service-alias-1", "test-service-1"),
                        Map.entry("test-service-alias-4", "test-service-4"),
                        Map.entry("test-service-alias-5", "test-service-5"),
                        Map.entry("test-service-alias-8", "test-service-8")
                )
        );
        when(dependencyFinder1.find(componentMetadata)).thenReturn(List.of(
                new Dependency("test-service-alias-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-alias-4"),
                new Dependency("test-service-alias-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-alias-8")
        ));
        when(dependencyFinder2.find(componentMetadata)).thenReturn(List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-8")
        ));

        // When
        List<Dependency> returnValue = underTest.getDependencies(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-3", "test-service-4"),
                new Dependency("test-service-5", "test-service-6"),
                new Dependency("test-service-7", "test-service-8")
        );
    }

    @Test
    public void getDependenciesShouldLogAndIgnoreAnExceptionWhenExecutingDependencyFinders() {
        // Given
        underTest = new MasterDependencyFinder(finderRegistry, componentAliasResolver);
        when(finderRegistry.getDependencyFinders()).thenReturn(List.of(dependencyFinder1, dependencyFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(Map.of());
        when(dependencyFinder1.id()).thenReturn("test-dependency-finder-1");
        when(dependencyFinder1.find(componentMetadata)).thenThrow(new RuntimeException("Fake exception"));
        Dependency dependency1 = new Dependency("test-service-1", "test-service-2");
        Dependency dependency2 = new Dependency("test-service-3", "test-service-4");
        when(dependencyFinder2.id()).thenReturn("test-dependency-finder-2");
        when(dependencyFinder2.find(componentMetadata)).thenReturn(List.of(dependency1, dependency2));

        // When
        List<Dependency> returnValue = underTest.getDependencies(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(dependency1, dependency2);
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.ERROR, "Failed to execute dependency finder test-dependency-finder-1"),
                new SimplifiedLogEvent(Level.INFO, "Dependency finder test-dependency-finder-2 found 2 dependencies"));
    }
}
