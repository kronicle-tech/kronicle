package tech.kronicle.plugins.manualdependencies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.DependencyDirection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualDependencyFinderTest {
    
    private ManualDependencyFinder underTest;
    
    @BeforeEach
    public void beforeEach() {
        underTest = new ManualDependencyFinder();
    }

    @Test
    public void idShouldReturnTheIdOfTheFinder() {
        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("manual-dependency");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheFinder() {
        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Finds dependencies manually specified in kronicle.yaml files.  ");
    }

    @Test
    public void findShouldFindNoDependenciesWhenComponentsIsEmpty() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldFindNoDependenciesWhenThereIsAComponentWithNoDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(Component.builder().build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void findShouldFindADependencyWhenAComponentHasADependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(new Dependency("test-component-1", "test-component-2"));
    }

    @Test
    public void findShouldFindAnDependencyWhenAComponentHasAnInboundDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").direction(DependencyDirection.INBOUND).build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(new Dependency("test-component-2", "test-component-1"));
    }

    @Test
    public void findShouldFindADependencyWhenAComponentHasAnOutboundDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").direction(DependencyDirection.OUTBOUND).build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(new Dependency("test-component-1", "test-component-2"));
    }

    @Test
    public void findShouldFindTwoDependenciesWhenAComponentHasTwoDependencies() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").build(),
                                        ComponentDependency.builder().targetComponentId("test-component-3").build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-component-1", "test-component-2"),
                new Dependency("test-component-1", "test-component-3"));
    }

    @Test
    public void findShouldFindFourDependenciesWhenTwoComponentsHaveTwoDependenciesEach() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").build(),
                                        ComponentDependency.builder().targetComponentId("test-component-3").build()))
                                .build(),
                        Component.builder()
                                .id("test-component-4")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-5").build(),
                                        ComponentDependency.builder().targetComponentId("test-component-6").build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-component-1", "test-component-2"),
                new Dependency("test-component-1", "test-component-3"),
                new Dependency("test-component-4", "test-component-5"),
                new Dependency("test-component-4", "test-component-6"));
    }

    @Test
    public void findShouldDeduplicateDependenciesForSameComponent() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").build(),
                                        ComponentDependency.builder().targetComponentId("test-component-2").build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-component-1", "test-component-2"));
    }

    @Test
    public void findShouldDeduplicateDependenciesOnDifferentComponents() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-2").build()))
                                .build(),
                        Component.builder()
                                .id("test-component-2")
                                .dependencies(List.of(
                                        ComponentDependency.builder().targetComponentId("test-component-1").direction(DependencyDirection.INBOUND).build()))
                                .build()))
                .build();

        // When
        List<Dependency> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                new Dependency("test-component-1", "test-component-2"));
    }
}
