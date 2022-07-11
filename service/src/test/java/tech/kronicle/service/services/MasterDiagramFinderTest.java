package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.DiagramFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.service.testutils.DiagramUtils.createDiagram;

@ExtendWith(MockitoExtension.class)
public class MasterDiagramFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private MasterDiagramFinder underTest;
    @Mock
    private FinderExtensionRegistry finderRegistry;
    @Mock
    private DiagramFinder diagramFinder1;
    @Mock
    private DiagramFinder diagramFinder2;
    private LogCaptor logCaptor;
    private LogCaptor taskExecutorLogCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(MasterDiagramFinder.class);
        taskExecutorLogCaptor = new LogCaptor(ExtensionExecutor.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
        taskExecutorLogCaptor.close();
    }

    @Test
    public void findDiagramsShouldReturnAllDiagramsFromAllDiagramFindersWithDiscoveredSetToTrue() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getDiagramFinders()).thenReturn(List.of(diagramFinder1, diagramFinder2));
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        Diagram diagram3 = createDiagram(3);
        Diagram diagram4 = createDiagram(4);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(diagramFinder1.find(componentMetadata)).thenReturn(createOutput(diagram1, diagram2));
        when(diagramFinder2.find(componentMetadata)).thenReturn(createOutput(diagram3, diagram4));

        // When
        List<Diagram> returnValue = underTest.findDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true),
                diagram3.withDiscovered(true),
                diagram4.withDiscovered(true)
        );
    }

    private Output<List<Diagram>, Void> createOutput(
            Diagram diagram1, 
            Diagram diagram2
    ) {
        return Output.ofOutput(
                List.of(diagram1, diagram2),
                CACHE_TTL
        );
    }

    @Test
    public void findDiagramsShouldLogAndIgnoreAnExceptionWhenExecutingDiagramFinders() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getDiagramFinders()).thenReturn(List.of(diagramFinder1, diagramFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(diagramFinder1.id()).thenReturn("test-diagram-finder-1");
        when(diagramFinder1.find(componentMetadata)).thenThrow(new RuntimeException("Fake exception"));
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        when(diagramFinder2.id()).thenReturn("test-diagram-finder-2");
        when(diagramFinder2.find(componentMetadata)).thenReturn(createOutput(diagram1, diagram2));

        // When
        List<Diagram> returnValue = underTest.findDiagrams(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true)
        );
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Diagram finder test-diagram-finder-2 found 2 diagrams")
        );
        assertThat(taskExecutorLogCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing finder test-diagram-finder-1"),
                new SimplifiedLogEvent(Level.ERROR, "Failed to execute finder test-diagram-finder-1"),
                new SimplifiedLogEvent(Level.INFO, "Executing finder test-diagram-finder-2")
        );
    }

    private MasterDiagramFinder createUnderTest() {
        ThrowableToScannerErrorMapper throwableToScannerErrorMapper = new ThrowableToScannerErrorMapper();
        return new MasterDiagramFinder(
                finderRegistry,
                new ExtensionExecutor(
                        new ExtensionOutputCache(
                                new ExtensionOutputCacheLoader(),
                                new ExtensionOutputCacheExpiry()
                        ),
                        throwableToScannerErrorMapper
                )
        );
    }

    private Diagram withDifferentName(Diagram diagram) {
        return diagram.withName("Same id but different name");
    }
}
