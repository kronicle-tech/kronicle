package tech.kronicle.plugins.structurediagram.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.ComponentConnection;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.DiagramConnection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.plugins.structurediagram.testutils.ComponentConnectionUtils.createComponentConnection;
import static tech.kronicle.plugins.structurediagram.testutils.ComponentUtils.createComponent;

public class StructureDiagramCreatorTest {

    private final StructureDiagramCreator underTest = new StructureDiagramCreator(new EnvironmentSplitter());

    @Test
    public void createStructureDiagramsShouldReturnAnEmptyListWhenComponentMetadataContainsNoComponents() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void createStructureDiagramsShouldReturnAnEmptyListWhenComponentMetadataContainsComponentsWithNoConnections() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        createComponent(1),
                        createComponent(2)
                ))
                .build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void createStructureDiagramsShouldReturnAnEmptyListWhenComponentMetadataContainsComponentsWithConnectionsWithNoConnectionTypes() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        createComponent(1, List.of(
                                createComponentConnection(1, 1),
                                createComponentConnection(1, 2)
                        )),
                        createComponent(2, List.of(
                                createComponentConnection(2, 1),
                                createComponentConnection(2, 2)
                        ))
                ))
                .build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void createStructureDiagramsShouldReturnADiagramWhenComponentMetadataContainsComponentsWithSubComponentConnections() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        createComponent(1, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-2")
                                        .type("sub-component")
                                        .build()
                        )),
                        createComponent(2, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-3")
                                        .type("sub-component")
                                        .build()
                        ))
                ))
                .build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                Diagram.builder()
                        .id("structure-test-component-id-1")
                        .name("Structure - test-component-id-1")
                        .discovered(true)
                        .description("An auto-generated diagram that shows the structure of the test-component-id-1 component")
                        .connections(List.of(
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-1")
                                        .targetComponentId("test-component-id-2")
                                        .type("sub-component")
                                        .build(),
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-2")
                                        .targetComponentId("test-component-id-3")
                                        .type("sub-component")
                                        .build()
                        ))
                        .build()
        );
    }

    @Test
    public void createStructureDiagramsShouldReturnADiagramWhenComponentMetadataContainsComponentsWithSuperComponentConnections() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        createComponent(1, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-2")
                                        .type("super-component")
                                        .build()
                        )),
                        createComponent(2, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-3")
                                        .type("super-component")
                                        .build()
                        ))
                ))
                .build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                Diagram.builder()
                        .id("structure-test-component-id-3")
                        .name("Structure - test-component-id-3")
                        .discovered(true)
                        .description("An auto-generated diagram that shows the structure of the test-component-id-3 component")
                        .connections(List.of(
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-2")
                                        .targetComponentId("test-component-id-1")
                                        .type("sub-component")
                                        .build(),
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-3")
                                        .targetComponentId("test-component-id-2")
                                        .type("sub-component")
                                        .build()
                        ))
                        .build()
        );
    }

    @Test
    public void createStructureDiagramsShouldReturnTwoDiagramsWhenComponentMetadataContainsComponentsWithUnrelatedConnections() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        createComponent(1, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-2")
                                        .type("sub-component")
                                        .build()
                        )),
                        createComponent(2, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-3")
                                        .type("sub-component")
                                        .build()
                        )),
                        createComponent(11, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-12")
                                        .type("sub-component")
                                        .build()
                        )),
                        createComponent(12, List.of(
                                ComponentConnection.builder()
                                        .targetComponentId("test-component-id-13")
                                        .type("sub-component")
                                        .build()
                        ))
                ))
                .build();

        // When
        List<Diagram> returnValue = underTest.createStructureDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                Diagram.builder()
                        .id("structure-test-component-id-1")
                        .name("Structure - test-component-id-1")
                        .discovered(true)
                        .description("An auto-generated diagram that shows the structure of the test-component-id-1 component")
                        .connections(List.of(
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-1")
                                        .targetComponentId("test-component-id-2")
                                        .type("sub-component")
                                        .build(),
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-2")
                                        .targetComponentId("test-component-id-3")
                                        .type("sub-component")
                                        .build()
                        ))
                        .build(),
                Diagram.builder()
                        .id("structure-test-component-id-11")
                        .name("Structure - test-component-id-11")
                        .discovered(true)
                        .description("An auto-generated diagram that shows the structure of the test-component-id-11 component")
                        .connections(List.of(
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-11")
                                        .targetComponentId("test-component-id-12")
                                        .type("sub-component")
                                        .build(),
                                DiagramConnection.builder()
                                        .sourceComponentId("test-component-id-12")
                                        .targetComponentId("test-component-id-13")
                                        .type("sub-component")
                                        .build()
                        ))
                        .build()
        );
    }
}
