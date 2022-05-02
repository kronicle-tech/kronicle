package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.ComponentFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentMetadata;
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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentFinder1.find(componentMetadata)).thenReturn(Output.ofOutput(List.of(component1, component2), CACHE_TTL));
        when(componentFinder2.find(componentMetadata)).thenReturn(Output.ofOutput(List.of(component3, component4), CACHE_TTL));

        // When
        List<Component> returnValue = underTest.findComponents(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true),
                component3.withDiscovered(true),
                component4.withDiscovered(true)
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
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(componentFinder1.find(componentMetadata)).thenReturn(Output.ofOutput(
                List.of(component1, component2),
                CACHE_TTL
        ));
        when(componentFinder2.find(componentMetadata)).thenReturn(Output.ofOutput(
                List.of(withDifferentName(component2), component3),
                CACHE_TTL
        ));

        // When
        List<Component> returnValue = underTest.findComponents(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true),
                component3.withDiscovered(true)
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
        ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .components(List.of(
                        component1,
                        component3
                ))
                .build();
        when(componentFinder1.find(componentMetadata)).thenReturn(Output.ofOutput(
                List.of(withDifferentName(component1), component2),
                CACHE_TTL
        ));
        when(componentFinder2.find(componentMetadata)).thenReturn(Output.ofOutput(
                List.of(withDifferentName(component3), component4),
                CACHE_TTL
        ));

        // When
        List<Component> returnValue = underTest.findComponents(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                component2.withDiscovered(true),
                component4.withDiscovered(true)
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
        when(componentFinder2.id()).thenReturn("test-component-finder-2");
        when(componentFinder2.find(componentMetadata)).thenReturn(Output.ofOutput(List.of(component1, component2), CACHE_TTL));

        // When
        List<Component> returnValue = underTest.findComponents(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                component1.withDiscovered(true),
                component2.withDiscovered(true)
        );
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.INFO, "Component finder test-component-finder-2 found 2 components")
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
}
