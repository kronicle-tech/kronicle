package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.sdk.models.GraphState;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.services.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.tracingprocessor.internal.testutils.CollatorGraphUtils.createCollatorGraph;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

@ExtendWith(MockitoExtension.class)
public class TracingProcessorTest {

    @Mock
    private ComponentGraphCollator componentGraphCollator;
    @Mock
    private SubComponentGraphCollator subComponentGraphCollator;
    @Mock
    private CallGraphCollator callGraphCollator;
    @Mock
    private EdgeHelper edgeHelper;
    @Mock
    private EdgeDurationCalculator edgeDurationCalculator;

    @Test
    public void processShouldProduceComponentDependenciesAndSubComponentDependenciesAndCallGraphs() {
        // Given
        TracingProcessor underTest = new TracingProcessor(
                componentGraphCollator,
                subComponentGraphCollator,
                callGraphCollator,
                edgeHelper,
                edgeDurationCalculator
        );

        TracingData tracingData = createTracingData(1);

        CollatorGraph graph1 = createCollatorGraph(1);
        CollatorGraph graph2 = createCollatorGraph(2);
        List<CollatorGraph> graphs1 = List.of(
                createCollatorGraph(3),
                createCollatorGraph(4)
        );

        when(subComponentGraphCollator.collateGraph(
                tracingData
        )).thenReturn(graph1);
        when(componentGraphCollator.collateGraph(
                tracingData
        )).thenReturn(graph2);
        when(callGraphCollator.collateCallGraphs(
                tracingData
        )).thenReturn(graphs1);

        // When
        List<Diagram> returnValue = underTest.process(tracingData);

        // Then
        assertThat(returnValue).containsExactly(
                Diagram.builder()
                        .id("test-tracing-data-id-1")
                        .name("Test Tracing Data 1")
                        .discovered(true).type("tracing")
                        .states(List.of(
                                GraphState.builder().pluginId("test-plugin-id-1").environmentId("test-environment-id-1").nodes(List.of(GraphNode.builder().componentId("test-component-id-2").build())).build()
                        ))
                        .build(),
                Diagram.builder()
                        .id("test-tracing-data-id-1")
                        .name("Test Tracing Data 1")
                        .discovered(true).type("tracing")
                        .states(List.of(
                                GraphState.builder().pluginId("test-plugin-id-1").environmentId("test-environment-id-1").nodes(List.of(GraphNode.builder().componentId("test-component-id-1").build())).build()
                        ))
                        .build(),
                Diagram.builder()
                        .id("test-tracing-data-id-1")
                        .name("Test Tracing Data 1")
                        .discovered(true).type("tracing")
                        .states(List.of(
                                GraphState.builder().pluginId("test-plugin-id-1").environmentId("test-environment-id-1").nodes(List.of(GraphNode.builder().componentId("test-component-id-3").build())).build()
                        ))
                        .build(),
                Diagram.builder()
                        .id("test-tracing-data-id-1")
                        .name("Test Tracing Data 1")
                        .discovered(true).type("tracing")
                        .states(List.of(
                                GraphState.builder().pluginId("test-plugin-id-1").environmentId("test-environment-id-1").nodes(List.of(GraphNode.builder().componentId("test-component-id-4").build())).build()
                        ))
                        .build()
        );
    }
}
