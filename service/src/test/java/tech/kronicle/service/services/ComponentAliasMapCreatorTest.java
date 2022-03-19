package tech.kronicle.service.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Alias;
import tech.kronicle.sdk.models.Component;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentAliasMapCreatorTest {

    private final ComponentAliasMapCreator underTest = new ComponentAliasMapCreator();

    @Test
    void createComponentAliasMapWhenComponentsIsEmptyShouldReturnAnEmptyMap() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    void createComponentAliasMapShouldMapAComponentIdToItself() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                Map.entry("test-component-id-1", "test-component-id-1")
        );
    }

    @Test
    void createComponentAliasMapShouldMapAComponentAliasToItsAssociatedComponentId() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-1").build()
                                ))
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("test-component-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-1", "test-component-id-1")
        ));
    }

    @Test
    void createComponentAliasMapShouldMapMultipleComponentAliasesForSameComponentToTheirAssociatedComponentId() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-1").build(),
                                        Alias.builder().id("test-alias-id-2").build()
                                ))
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("test-component-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-2", "test-component-id-1")
        ));
    }

    @Test
    void createComponentAliasMapShouldMapMultipleComponentAliasesForMultipleComponentsToTheirAssociatedComponentIds() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-1").build(),
                                        Alias.builder().id("test-alias-id-2").build()
                                ))
                                .build(),
                        Component.builder()
                                .id("test-component-id-2")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-3").build(),
                                        Alias.builder().id("test-alias-id-4").build()
                                ))
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("test-component-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-2", "test-component-id-1"),
                Map.entry("test-component-id-2", "test-component-id-2"),
                Map.entry("test-alias-id-3", "test-component-id-2"),
                Map.entry("test-alias-id-4", "test-component-id-2")
        ));
    }

    @Test
    void createComponentAliasMapShouldIgnoreDuplicateAliasesForTheSameComponent() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-1").build(),
                                        Alias.builder().id("test-alias-id-2").build(),
                                        Alias.builder().id("test-alias-id-2").build(),
                                        Alias.builder().id("test-alias-id-3").build()
                                ))
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("test-component-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-2", "test-component-id-1"),
                Map.entry("test-alias-id-3", "test-component-id-1")
        ));
    }

    @Test
    void createComponentAliasMapShouldIgnoreDuplicateAliasesAcrossMultipleComponents() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        Component.builder()
                                .id("test-component-id-1")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-1").build(),
                                        Alias.builder().id("test-alias-id-2").build()
                                ))
                                .build(),
                        Component.builder()
                                .id("test-component-id-2")
                                .aliases(List.of(
                                        Alias.builder().id("test-alias-id-2").build(),
                                        Alias.builder().id("test-alias-id-3").build()
                                ))
                                .build()
                ))
                .build();

        // When
        Map<String, String> returnValue = underTest.createComponentAliasMap(componentMetadata);

        // Then
        assertThat(returnValue).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("test-component-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-1", "test-component-id-1"),
                Map.entry("test-alias-id-2", "test-component-id-1"),
                Map.entry("test-component-id-2", "test-component-id-2"),
                Map.entry("test-alias-id-3", "test-component-id-2")
        ));
    }
}