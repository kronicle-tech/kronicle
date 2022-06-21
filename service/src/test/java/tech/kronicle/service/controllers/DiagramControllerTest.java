package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.GetDiagramResponse;
import tech.kronicle.sdk.models.GetDiagramsResponse;
import tech.kronicle.service.services.ComponentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;

@ExtendWith(MockitoExtension.class)
public class DiagramControllerTest {

    private static final Diagram DIAGRAM_1 = createDiagram(1);
    private static final Diagram DIAGRAM_2 = createDiagram(2);
    private static final List<Diagram> DIAGRAMS = List.of(DIAGRAM_1, DIAGRAM_2);
    
    @Mock
    private ComponentService mockComponentService;
    private DiagramController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new DiagramController(mockComponentService);
    }

    @Test
    public void getDiagramsShouldReturnDiagrams() {
        // Given
        when(mockComponentService.getDiagrams(List.of())).thenReturn(DIAGRAMS);

        // When
        GetDiagramsResponse returnValue = underTest.getDiagrams(List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagrams()).containsExactlyElementsOf(DIAGRAMS);
    }

    @Test
    public void getDiagramsShouldFilterStateTypes() {
        // Given
        List<String> stateTypes = List.of("test-state-type-1", "test-state-type-2");
        when(mockComponentService.getDiagrams(stateTypes)).thenReturn(DIAGRAMS);

        // When
        GetDiagramsResponse returnValue = underTest.getDiagrams(stateTypes);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagrams()).containsExactlyElementsOf(DIAGRAMS);
    }

    @Test
    public void getDiagramShouldReturnADiagram() {
        // Given
        when(mockComponentService.getDiagram(DIAGRAM_1.getId(), List.of())).thenReturn(DIAGRAM_1);

        // When
        GetDiagramResponse returnValue = underTest.getDiagram(DIAGRAM_1.getId(), List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagram()).isSameAs(DIAGRAM_1);
    }

    @Test
    public void getDiagramShouldNotReturnADiagramWhenDiagramIdIsUnknown() {
        // Given
        String diagramId = "unknown";
        when(mockComponentService.getDiagram(diagramId, List.of())).thenReturn(null);

        // When
        GetDiagramResponse returnValue = underTest.getDiagram(diagramId, List.of());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagram()).isNull();
    }

    @Test
    public void getDiagramShouldFilterStateTypes() {
        // Given
        String diagramId = "unknown";
        List<String> stateTypes = List.of("test-state-type-1", "test-state-type-2");
        when(mockComponentService.getDiagram(diagramId, stateTypes)).thenReturn(DIAGRAM_1);

        // When
        GetDiagramResponse returnValue = underTest.getDiagram(diagramId, stateTypes);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagram()).isEqualTo(DIAGRAM_1);
    }
}
