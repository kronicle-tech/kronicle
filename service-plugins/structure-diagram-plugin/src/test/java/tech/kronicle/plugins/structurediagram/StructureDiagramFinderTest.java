package tech.kronicle.plugins.structurediagram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.structurediagram.services.StructureDiagramCreator;
import tech.kronicle.plugintestutils.scanners.BaseScannerTest;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.structurediagram.testutils.DiagramUtils.createDiagram;

@ExtendWith(MockitoExtension.class)
public class StructureDiagramFinderTest extends BaseScannerTest {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Mock
    private StructureDiagramCreator structureDiagramCreator;

    @Test
    public void idShouldReturnTheIdOfTheScanner() {
        // Given
        StructureDiagramFinder underTest = createUnderTest();

        // When
        String returnValue = underTest.id();

        // Then
        assertThat(returnValue).isEqualTo("structure-diagram");
    }

    @Test
    public void descriptionShouldReturnTheDescriptionOfTheScanner() {
        // Given
        StructureDiagramFinder underTest = createUnderTest();

        // When
        String returnValue = underTest.description();

        // Then
        assertThat(returnValue).isEqualTo("Creates structure diagrams from the connections on components");
    }

    @Test
    public void notesShouldReturnNull() {
        // Given
        StructureDiagramFinder underTest = createUnderTest();

        // When
        String returnValue = underTest.notes();

        // Then
        assertThat(returnValue).isNull();
    }

    @Test
    public void findShouldReturnAllDiagrams() {
        // Given
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        List<Diagram> diagrams = List.of(
                createDiagram(1),
                createDiagram(2)
        );
        StructureDiagramFinder underTest = createUnderTest();
        when(structureDiagramCreator.createStructureDiagrams(componentMetadata)).thenReturn(diagrams);

        // When
        Output<List<Diagram>, Void> returnValue = underTest.find(componentMetadata);

        // Then
        assertThat(returnValue).isEqualTo(Output.ofOutput(diagrams, CACHE_TTL));
    }

    private StructureDiagramFinder createUnderTest() {
        return new StructureDiagramFinder(structureDiagramCreator);
    }
}
