package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.Diagram;
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
        when(mockComponentService.getDiagrams()).thenReturn(DIAGRAMS);

        // When
        GetDiagramsResponse returnValue = underTest.getDiagrams();

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagrams()).containsExactlyElementsOf(DIAGRAMS);
    }
}
