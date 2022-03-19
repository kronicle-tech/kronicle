package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createCallGraphs;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createComponentDependencies;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createSubComponentDependencies;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

@ExtendWith(MockitoExtension.class)
public class TracingProcessorTest {

    @Mock
    private ComponentDependencyCollator componentDependencyCollator;
    @Mock
    private SubComponentDependencyCollator subComponentDependencyCollator;
    @Mock
    private CallGraphCollator callGraphCollator;

    @Test
    public void processShouldProduceComponentDependenciesAndSubComponentDependenciesAndCallGraphs() {
        // Given
        TracingProcessor underTest = new TracingProcessor(
                componentDependencyCollator,
                subComponentDependencyCollator,
                callGraphCollator
        );

        TracingData tracingData1 = createTracingData(1);
        TracingData tracingData2 = createTracingData(2);
        List<TracingData> tracingData = List.of(
                tracingData1,
                tracingData2
        );
        List<Dependency> dependencies = Stream.of(tracingData1, tracingData2)
                .map(TracingData::getDependencies)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<GenericTrace> traces = Stream.of(tracingData1, tracingData2)
                .map(TracingData::getTraces)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        SummaryComponentDependencies componentDependencies = createComponentDependencies();
        SummarySubComponentDependencies subComponentDependencies = createSubComponentDependencies();
        List<SummaryCallGraph> callGraphs = createCallGraphs();

        when(subComponentDependencyCollator.collateDependencies(
                traces
        )).thenReturn(subComponentDependencies);
        when(componentDependencyCollator.collateDependencies(
                traces,
                dependencies
        )).thenReturn(componentDependencies);
        when(callGraphCollator.collateCallGraphs(
                traces
        )).thenReturn(callGraphs);

        // When
        ProcessedTracingData returnValue = underTest.process(tracingData);

        // Then
        assertThat(returnValue).isEqualTo(
                new ProcessedTracingData(
                        componentDependencies,
                        subComponentDependencies,
                        callGraphs
                )
        );
    }
}
