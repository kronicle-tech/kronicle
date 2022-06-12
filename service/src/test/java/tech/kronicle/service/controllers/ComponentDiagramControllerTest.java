package tech.kronicle.service.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.sdk.models.GetComponentDiagramsResponse;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.service.services.ComponentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;

@ExtendWith(MockitoExtension.class)
public class ComponentDiagramControllerTest {

    @Mock
    private ComponentService mockComponentService;
    private ComponentDiagramController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new ComponentDiagramController(mockComponentService);
    }

    @Test
    public void getComponentDiagramsShouldReturnComponentDiagrams() {
        // Given
        String componentId = "test-component-id-1";
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        when(mockComponentService.getComponentDiagrams(componentId)).thenReturn(List.of(diagram1, diagram2));

        // When
        GetComponentDiagramsResponse returnValue = underTest.getComponentDiagrams(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagrams()).containsExactly(diagram1, diagram2);
    }

    @Test
    public void getComponentDiagramsShouldNotReturnComponentDiagramsWhenComponentIdIsUnknown() {
        // Given
        String componentId = "unknown";
        when(mockComponentService.getComponentDiagrams(componentId)).thenReturn(List.of());

        // When
        GetComponentDiagramsResponse returnValue = underTest.getComponentDiagrams(componentId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getDiagrams()).isEmpty();
    }
}
