package tech.kronicle.plugins.manualdependencies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentDependency;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.DependencyDirection;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualDependencyFinderTest {

    private static final Duration CACHE_TTL = Duration.ZERO;

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
        assertThat(returnValue).isEqualTo("Finds dependencies manually specified in kronicle.yaml files");
    }

    @Test
    public void findShouldFindNoDependenciesWhenComponentsIsEmpty() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(TracingData.EMPTY));
    }

    @Test
    public void findShouldFindNoDependenciesWhenThereIsAComponentWithNoDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(Component.builder().build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(TracingData.EMPTY));
    }

    @Test
    public void findShouldFindADependencyWhenAComponentHasADependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2").build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2")
        ));
    }

    @Test
    public void findShouldFindAnDependencyWhenAComponentHasAnInboundDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2")
                                                .direction(DependencyDirection.INBOUND)
                                                .build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-2", "test-component-1")
        ));
    }

    @Test
    public void findShouldSetTheDependencyTypeIdToCompositionWhenAComponentHasDependencyWithNoTypeId() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2")
                                                .typeId(null)
                                                .build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2")
                        .withTypeId("composition")
        ));
    }

    @Test
    public void findShouldFindADependencyWhenAComponentHasAnOutboundDependency() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2")
                                                .direction(DependencyDirection.OUTBOUND)
                                                .build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2")
        ));
    }

    @Test
    public void findShouldFindTwoDependenciesWhenAComponentHasTwoDependencies() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2").build(),
                                        createComponentDependencyBuilder("test-component-3").build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2"),
                createDependency("test-component-1", "test-component-3")
        ));
    }

    @Test
    public void findShouldFindFourDependenciesWhenTwoComponentsHaveTwoDependenciesEach() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2").build(),
                                        createComponentDependencyBuilder("test-component-3").build()
                                ))
                                .build(),
                        Component.builder()
                                .id("test-component-4")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-5").build(),
                                        createComponentDependencyBuilder("test-component-6").build()
                                ))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2"),
                createDependency("test-component-1", "test-component-3"),
                createDependency("test-component-4", "test-component-5"),
                createDependency("test-component-4", "test-component-6")
        ));
    }

    @Test
    public void findShouldDeduplicateDependenciesForSameComponent() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2").build(),
                                        createComponentDependencyBuilder("test-component-2").build()))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2")
        ));
    }

    @Test
    public void findShouldDeduplicateDependenciesOnDifferentComponents() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-1")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-2").build()))
                                .build(),
                        Component.builder()
                                .id("test-component-2")
                                .dependencies(List.of(
                                        createComponentDependencyBuilder("test-component-1").direction(DependencyDirection.INBOUND).build()))
                                .build()))
                .build();

        // When
        Output<TracingData, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(createTracingDataOutput(
                createDependency("test-component-1", "test-component-2")
        ));
    }

    private Output<TracingData, Void> createTracingDataOutput(Dependency... dependencies) {
        return createTracingDataOutput(createTracingData(dependencies));
    }

    private Output<TracingData, Void> createTracingDataOutput(TracingData tracingData) {
        return Output.ofOutput(
                tracingData,
                CACHE_TTL
        );
    }

    private ComponentDependency.ComponentDependencyBuilder createComponentDependencyBuilder(String targetComponentId) {
        return ComponentDependency.builder()
                .targetComponentId(targetComponentId)
                .typeId("test-type-id")
                .label("test-label")
                .description("test-description");
    }

    private TracingData createTracingData(Dependency... dependencies) {
        return TracingData.builder()
                .dependencies(Arrays.asList(dependencies))
                .build();
    }

    private Dependency createDependency(String sourceComponentId, String targetComponentId) {
        return new Dependency(
                sourceComponentId,
                targetComponentId,
                "test-type-id",
                "test-label",
                "test-description"
        );
    }
}
