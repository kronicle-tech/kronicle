package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.finders.models.ComponentsAndDiagrams;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MasterComponentFinderTest {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private MasterComponentFinder underTest;
    @Mock
    private FinderExtensionRegistry finderRegistry;
    @Mock
    private ComponentFinder componentFinder1;
    @Mock
    private ComponentFinder componentFinder2;
    private LogCaptor logCaptor;
    private LogCaptor taskExecutorLogCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(MasterComponentFinder.class);
        taskExecutorLogCaptor = new LogCaptor(ExtensionExecutor.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
        taskExecutorLogCaptor.close();
    }

    @Test
    public void findComponentsShouldReturnAllComponentsFromAllComponentFindersWithDiscoveredSetToTrue() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getComponentFinders()).thenReturn(List.of(componentFinder1, componentFinder2));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        Diagram diagram3 = createDiagram(3);
        Diagram diagram4 = createDiagram(4);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentFinder1.find(componentMetadata)).thenReturn(createOutput(component1, component2, diagram1, diagram2));
        when(componentFinder2.find(componentMetadata)).thenReturn(createOutput(component3, component4, diagram3, diagram4));

        // When
        ComponentsAndDiagrams returnValue = underTest.findComponentsAndDiagrams(componentMetadata);

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true),
                component3.withDiscovered(true),
                component4.withDiscovered(true)
        );
        assertThat(returnValue.getDiagrams()).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true),
                diagram3.withDiscovered(true),
                diagram4.withDiscovered(true)
        );
    }

    private Output<ComponentsAndDiagrams, Void> createOutput(
            Component component1, 
            Component component2,
            Diagram diagram1,
            Diagram diagram2
    ) {
        return Output.ofOutput(
                new ComponentsAndDiagrams(
                        List.of(component1, component2),
                        List.of(diagram1, diagram2)
                ),
                CACHE_TTL
        );
    }

    @Test
    public void findComponentsShouldDeduplicateComponentsByComponentId() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getComponentFinders()).thenReturn(List.of(componentFinder1, componentFinder2));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        Diagram diagram3 = createDiagram(3);
        Diagram diagram4 = createDiagram(4);
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentFinder1.find(componentMetadata)).thenReturn(createOutput(component1, component2, diagram1, diagram2));
        when(componentFinder2.find(componentMetadata)).thenReturn(createOutput(withDifferentName(component2), component3, diagram3, diagram4));

        // When
        ComponentsAndDiagrams returnValue = underTest.findComponentsAndDiagrams(componentMetadata);

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true),
                component3.withDiscovered(true)
        );
        assertThat(returnValue.getDiagrams()).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true),
                diagram3.withDiscovered(true),
                diagram4.withDiscovered(true)
        );
    }

    @Test
    public void findComponentsShouldDeduplicateComponentsAlreadyInComponentMetadataByComponentId() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getComponentFinders()).thenReturn(List.of(componentFinder1, componentFinder2));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Component component3 = createComponent(3);
        Component component4 = createComponent(4);
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        Diagram diagram3 = createDiagram(3);
        Diagram diagram4 = createDiagram(4);
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        component1,
                        component3
                ))
                .build();
        when(componentFinder1.find(componentMetadata)).thenReturn(createOutput(withDifferentName(component1), component2, diagram1, diagram2));
        when(componentFinder2.find(componentMetadata)).thenReturn(createOutput(withDifferentName(component3), component4, diagram3, diagram4));

        // When
        ComponentsAndDiagrams returnValue = underTest.findComponentsAndDiagrams(componentMetadata);

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component2.withDiscovered(true),
                component4.withDiscovered(true)
        );
        assertThat(returnValue.getDiagrams()).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true),
                diagram3.withDiscovered(true),
                diagram4.withDiscovered(true)
        );
    }

    @Test
    public void findComponentsShouldLogAndIgnoreAnExceptionWhenExecutingComponentFinders() {
        // Given
        underTest = createUnderTest();
        when(finderRegistry.getComponentFinders()).thenReturn(List.of(componentFinder1, componentFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentFinder1.id()).thenReturn("test-component-finder-1");
        when(componentFinder1.find(componentMetadata)).thenThrow(new RuntimeException("Fake exception"));
        Component component1 = createComponent(1);
        Component component2 = createComponent(2);
        Diagram diagram1 = createDiagram(1);
        Diagram diagram2 = createDiagram(2);
        when(componentFinder2.id()).thenReturn("test-component-finder-2");
        when(componentFinder2.find(componentMetadata)).thenReturn(createOutput(component1, component2, diagram1, diagram2));

        // When
        ComponentsAndDiagrams returnValue = underTest.findComponentsAndDiagrams(componentMetadata);

        // Then
        assertThat(returnValue.getComponents()).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true)
        );
        assertThat(returnValue.getDiagrams()).containsExactly(
                diagram1.withDiscovered(true),
                diagram2.withDiscovered(true)
        );
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Component finder test-component-finder-2 found 2 components and 2 diagrams")
        );
        assertThat(taskExecutorLogCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Executing finder test-component-finder-1"),
                new SimplifiedLogEvent(Level.ERROR, "Failed to execute finder test-component-finder-1"),
                new SimplifiedLogEvent(Level.INFO, "Executing finder test-component-finder-2")
        );
    }

    private MasterComponentFinder createUnderTest() {
        ThrowableToScannerErrorMapper throwableToScannerErrorMapper = new ThrowableToScannerErrorMapper();
        return new MasterComponentFinder(
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

    private Component createComponent(int componentNumber) {
        return Component.builder()
                .id("test-component-" + componentNumber)
                .build();
    }

    private Component withDifferentName(Component component) {
        return component.withName("Same id but different name");
    }

    private Diagram createDiagram(int diagramNumber) {
        return Diagram.builder()
                .id("test-diagram-id-" + diagramNumber)
                .build();
    }
}
