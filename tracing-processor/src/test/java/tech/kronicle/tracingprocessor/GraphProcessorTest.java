package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.GraphEdge;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.sdk.models.GraphState;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.TimestampsForEdge;
import tech.kronicle.tracingprocessor.internal.services.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.tracingprocessor.internal.testutils.CollatorGraphUtils.createCollatorGraph;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

@ExtendWith(MockitoExtension.class)
public class GraphProcessorTest {

    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.of(1, 1, 1, 0, 0);
    public static final LocalDateTime END_TIMESTAMP = LocalDateTime.of(2, 1, 1, 0, 0);
    @Mock
    private DiagramGraphCollator diagramGraphCollator;
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
    public void processTracingDataShouldProduceComponentDependenciesAndSubComponentDependenciesAndCallGraphs() {
        // Given
        GraphProcessor underTest = new GraphProcessor(
                diagramGraphCollator,
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

        when(componentGraphCollator.collateGraph(
                tracingData
        )).thenReturn(graph1);
        when(subComponentGraphCollator.collateGraph(
                tracingData
        )).thenReturn(graph2);
        when(callGraphCollator.collateCallGraphs(
                tracingData
        )).thenReturn(graphs1);
        when(edgeHelper.getTimestampsForEdge(List.of(1L, 2L))).thenReturn(
                new TimestampsForEdge(
                        START_TIMESTAMP,
                        END_TIMESTAMP
                )
        );

        // When
        List<Diagram> returnValue = underTest.processTracingData(tracingData);

        // Then
        assertThat(returnValue).containsExactly(
                createDiagram(1, 1, "", "", "tracing"),
                createDiagram(1, 2, "-subcomponents", " - Subcomponents", "tracing"),
                createDiagram(1, 3, "-call-graph-1", " - Call Graph 1", "call-graph"),
                createDiagram(1, 4, "-call-graph-2", " - Call Graph 2", "call-graph")
        );
    }

    private Diagram createDiagram(int tracingDataNumber, int diagramNumber, String idSuffix, String nameSuffix, String type) {
        return Diagram.builder()
                .id("test-tracing-data-id-" + tracingDataNumber + idSuffix)
                .name("Test Tracing Data " + tracingDataNumber + nameSuffix)
                .discovered(true)
                .type(type)
                .states(List.of(
                        GraphState.builder()
                                .pluginId("test-plugin-id-" + tracingDataNumber)
                                .environmentId("test-environment-id-" + tracingDataNumber)
                                .nodes(List.of(
                                        GraphNode.builder()
                                                .componentId("test-component-id-" + diagramNumber + "-1")
                                                .build(),
                                        GraphNode.builder()
                                                .componentId("test-component-id-" + diagramNumber + "-2")
                                                .build()
                                ))
                                .edges(List.of(
                                        GraphEdge.builder()
                                                .sourceIndex(0)
                                                .targetIndex(1)
                                                .startTimestamp(START_TIMESTAMP)
                                                .endTimestamp(END_TIMESTAMP)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
