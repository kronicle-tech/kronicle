package tech.kronicle.service.services;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.TracingDataFinder;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.testutils.LogCaptor;
import tech.kronicle.testutils.SimplifiedLogEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

@ExtendWith(MockitoExtension.class)
public class MasterTracingDataFinderTest {

    private MasterTracingDataFinder underTest;
    @Mock
    private FinderExtensionRegistry finderRegistry;
    @Mock
    private TracingDataFinder tracingDataFinder1;
    @Mock
    private TracingDataFinder tracingDataFinder2;
    private LogCaptor logCaptor;

    @BeforeEach
    public void beforeEach() {
        logCaptor = new LogCaptor(MasterTracingDataFinder.class);
    }

    @AfterEach
    public void afterEach() {
        logCaptor.close();
    }

    @Test
    public void findTracingDataShouldReturnAllTracingDataFromAllTracingDataFinders() {
        // Given
        underTest = new MasterTracingDataFinder(finderRegistry);
        when(finderRegistry.getTracingDataFinders()).thenReturn(List.of(tracingDataFinder1, tracingDataFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        TracingData tracingData1 = createTracingData(1);
        when(tracingDataFinder1.find(componentMetadata)).thenReturn(tracingData1);
        TracingData tracingData2 = createTracingData(2);
        when(tracingDataFinder2.find(componentMetadata)).thenReturn(tracingData2);

        // When
        List<TracingData> returnValue = underTest.findTracingData(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(tracingData1, tracingData2);
    }

    @Test
    public void findTracingDataShouldLogAndIgnoreAnExceptionWhenExecutingTracingDataFinders() {
        // Given
        underTest = new MasterTracingDataFinder(finderRegistry);
        when(finderRegistry.getTracingDataFinders()).thenReturn(List.of(tracingDataFinder1, tracingDataFinder2));
        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
        when(tracingDataFinder1.id()).thenReturn("test-tracing-data-finder-1");
        when(tracingDataFinder1.find(componentMetadata)).thenThrow(new RuntimeException("Fake exception"));
        when(tracingDataFinder2.id()).thenReturn("test-tracing-data-finder-2");
        TracingData tracingData1 = createTracingData(1);
        when(tracingDataFinder2.find(componentMetadata)).thenReturn(tracingData1);

        // When
        List<TracingData> returnValue = underTest.findTracingData(componentMetadata);

        // Then
        assertThat(returnValue).containsExactly(
                TracingData.builder().build(),
                tracingData1
        );
        assertThat(logCaptor.getSimplifiedEvents()).containsExactly(
                new SimplifiedLogEvent(Level.ERROR, "Failed to execute tracing data finder test-tracing-data-finder-1"),
                new SimplifiedLogEvent(Level.INFO, "Tracing data finder test-tracing-data-finder-2 found 2 dependencies"),
                new SimplifiedLogEvent(Level.INFO, "Tracing data finder test-tracing-data-finder-2 found 3 traces"));
    }

//    @Test
//    public void findDependenciesShouldDeduplicateDependencies() {
//        // Given
//        underTest = new MasterTracingDataFinder(finderRegistry, componentAliasResolver);
//        when(finderRegistry.getTracingDataFinders()).thenReturn(List.of(tracingDataFinder1, tracingDataFinder2));
//        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
//        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(Map.of());
//        TracingData tracingData1 = new TracingData("test-service-1", "test-service-2");
//        TracingData tracingData2 = new TracingData("test-service-3", "test-service-4");
//        when(tracingDataFinder1.find(componentMetadata)).thenReturn(List.of(tracingData1, tracingData2));
//        TracingData tracingData3 = new TracingData("test-service-5", "test-service-6");
//        when(tracingDataFinder2.find(componentMetadata)).thenReturn(List.of(tracingData2, tracingData3));
//
//        // When
//        List<TracingData> returnValue = underTest.findDependencies(componentMetadata);
//
//        // Then
//        assertThat(returnValue).containsExactly(tracingData1, tracingData2, tracingData3);
//    }

//    @Test
//    public void findDependenciesShouldMapComponentAliasIds() {
//        // Given
//        underTest = new MasterTracingDataFinder(finderRegistry, componentAliasResolver);
//        when(finderRegistry.getTracingDataFinders()).thenReturn(List.of(tracingDataFinder1, tracingDataFinder2));
//        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
//        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(
//                Map.ofEntries(
//                        Map.entry("test-service-alias-1", "test-service-1"),
//                        Map.entry("test-service-alias-4", "test-service-4"),
//                        Map.entry("test-service-alias-5", "test-service-5"),
//                        Map.entry("test-service-alias-8", "test-service-8")
//                )
//        );
//        when(tracingDataFinder1.find(componentMetadata)).thenReturn(List.of(
//                new TracingData("test-service-alias-1", "test-service-2"),
//                new TracingData("test-service-3", "test-service-alias-4")
//        ));
//        when(tracingDataFinder2.find(componentMetadata)).thenReturn(List.of(
//                new TracingData("test-service-alias-5", "test-service-6"),
//                new TracingData("test-service-7", "test-service-alias-8")
//        ));
//
//        // When
//        List<TracingData> returnValue = underTest.findDependencies(componentMetadata);
//
//        // Then
//        assertThat(returnValue).containsExactly(
//                new TracingData("test-service-1", "test-service-2"),
//                new TracingData("test-service-3", "test-service-4"),
//                new TracingData("test-service-5", "test-service-6"),
//                new TracingData("test-service-7", "test-service-8")
//        );
//    }

//    @Test
//    public void findDependenciesShouldDeduplicateMappedComponentAliasIds() {
//        // Given
//        underTest = new MasterTracingDataFinder(finderRegistry, componentAliasResolver);
//        when(finderRegistry.getTracingDataFinders()).thenReturn(List.of(tracingDataFinder1, tracingDataFinder2));
//        ComponentMetadata componentMetadata = ComponentMetadata.builder().build();
//        when(componentAliasResolver.createComponentAliasMap(componentMetadata)).thenReturn(
//                Map.ofEntries(
//                        Map.entry("test-service-alias-1", "test-service-1"),
//                        Map.entry("test-service-alias-4", "test-service-4"),
//                        Map.entry("test-service-alias-5", "test-service-5"),
//                        Map.entry("test-service-alias-8", "test-service-8")
//                )
//        );
//        when(tracingDataFinder1.find(componentMetadata)).thenReturn(List.of(
//                new TracingData("test-service-alias-1", "test-service-2"),
//                new TracingData("test-service-3", "test-service-alias-4"),
//                new TracingData("test-service-alias-5", "test-service-6"),
//                new TracingData("test-service-7", "test-service-alias-8")
//        ));
//        when(tracingDataFinder2.find(componentMetadata)).thenReturn(List.of(
//                new TracingData("test-service-1", "test-service-2"),
//                new TracingData("test-service-3", "test-service-4"),
//                new TracingData("test-service-5", "test-service-6"),
//                new TracingData("test-service-7", "test-service-8")
//        ));
//
//        // When
//        List<TracingData> returnValue = underTest.findDependencies(componentMetadata);
//
//        // Then
//        assertThat(returnValue).containsExactly(
//                new TracingData("test-service-1", "test-service-2"),
//                new TracingData("test-service-3", "test-service-4"),
//                new TracingData("test-service-5", "test-service-6"),
//                new TracingData("test-service-7", "test-service-8")
//        );
//    }
}
