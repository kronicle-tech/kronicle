package tech.kronicle.tracingprocessor.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.constants.GraphEdgeTypeIds;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.tracingprocessor.internal.services.TracingTestHelper.createTrace;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

public class CallGraphCollatorTest {

    private final TracingTestHelper testHelper = new TracingTestHelper();
    private final CallGraphCollator underTest = createCallGraphCollator();

    @Test
    public void collateCallGraphsShouldReturnNoCallGraphsWhenThereAreNoTraces() {
        // Given
        List<GenericTrace> traces = List.of();

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
        assertThat(returnValue).isEmpty();
    }
    
    @Test
    public void collateCallGraphsShouldReturnACallGraphWhenThereIsASingleTraceWithOneSpan() {
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1));

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
        assertThat(returnValue).containsExactly(CollatorGraph.builder()
                .sampleSize(1)
                .nodes(List.of(
                        GraphNode.builder()
                                .componentId("test-service-1")
                                .name("test-span-1")
                                .build()))
                .edges(List.of(
                        CollatorGraphEdge.builder()
                                .targetIndex(0)
                                .type(GraphEdgeTypeIds.TRACE)
                                .sampleSize(1)
                                .timestamps(List.of(1_000L))
                                .durations(List.of(1_000L))
                                .build()))
                .build());
    }

    @Test
    public void collateCallGraphsShouldReturnACallGraphWhenThereIsASingleTraceWithTwoConnectedSpans() {
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
        assertThat(returnValue).containsExactly(CollatorGraph.builder()
                .sampleSize(1)
                .nodes(List.of(
                        GraphNode.builder()
                                .componentId("test-service-1")
                                .name("test-span-1")
                                .build(),
                        GraphNode.builder()
                                .componentId("test-service-2")
                                .name("test-span-2")
                                .build()))
                .edges(List.of(
                        CollatorGraphEdge.builder()
                                .targetIndex(0)
                                .relatedIndexes(List.of(1))
                                .type(GraphEdgeTypeIds.TRACE)
                                .sampleSize(1)
                                .timestamps(List.of(1_000L))
                                .durations(List.of(1_000L))
                                .build(),
                        CollatorGraphEdge.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
                                .type(GraphEdgeTypeIds.TRACE)
                                .sampleSize(1)
                                .timestamps(List.of(2_000L))
                                .durations(List.of(2_000L))
                                .build()))
                .build());
    }

    @Test
    public void collateCallGraphsShouldMergeDuplicateEdgesInTheSameTraceAndSetSampleSizeToIncludeAllDuplicates() {
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2a = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span2b = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2a, trace1Span2b));

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
         assertThat(returnValue).containsExactly(CollatorGraph.builder()
                .sampleSize(1)
                .nodes(List.of(
                        GraphNode.builder()
                                .componentId("test-service-1")
                                .name("test-span-1")
                                .build(),
                        GraphNode.builder()
                                .componentId("test-service-2")
                                .name("test-span-2")
                                .build()))
                .edges(List.of(
                        CollatorGraphEdge.builder()
                                .targetIndex(0)
                                .relatedIndexes(List.of(1))
                                .type(GraphEdgeTypeIds.TRACE)
                                .sampleSize(1)
                                .timestamps(List.of(1_000L))
                                .durations(List.of(1_000L))
                                .build(),
                        CollatorGraphEdge.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
                                .type(GraphEdgeTypeIds.TRACE)
                                .sampleSize(2)
                                .timestamps(List.of(2_000L, 3_000L))
                                .durations(List.of(2_000L, 3_000L))
                                .build()))
                .build());
    }

    @Test
    public void collateCallGraphsShouldCreateSeparateCallGraphsForMultipleTraces() {
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace2Span1 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace2Span2 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .sourceName("test-service-4")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2));

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
        assertThat(returnValue).containsExactly(
                CollatorGraph.builder()
                        .sampleSize(1)
                        .nodes(List.of(
                                GraphNode.builder()
                                        .componentId("test-service-3")
                                        .name("test-span-3")
                                        .build(),
                                GraphNode.builder()
                                        .componentId("test-service-4")
                                        .name("test-span-4")
                                        .build()))
                        .edges(List.of(
                                CollatorGraphEdge.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(1)
                                        .timestamps(List.of(3_000L))
                                        .durations(List.of(3_000L))
                                        .build(),
                                CollatorGraphEdge.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(1)
                                        .timestamps(List.of(4_000L))
                                        .durations(List.of(4_000L))
                                        .build()))
                        .build(),
                CollatorGraph.builder()
                        .sampleSize(1)
                        .nodes(List.of(
                                GraphNode.builder()
                                        .componentId("test-service-1")
                                        .name("test-span-1")
                                        .build(),
                                GraphNode.builder()
                                        .componentId("test-service-2")
                                        .name("test-span-2")
                                        .build()))
                        .edges(List.of(
                                CollatorGraphEdge.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(1)
                                        .timestamps(List.of(1_000L))
                                        .durations(List.of(1_000L))
                                        .build(),
                                CollatorGraphEdge.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(1)
                                        .timestamps(List.of(2_000L))
                                        .durations(List.of(2_000L))
                                        .build()))
                        .build());
    }

    @Test
    public void collateCallGraphsShouldMergeDuplicateCallGraphs() {
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace2Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace2Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2));

        // When
        List<CollatorGraph> returnValue = underTest.collateCallGraphs(createTracingData(traces));

        // Then
        assertThat(returnValue).containsExactly(
                CollatorGraph.builder()
                        .sampleSize(2)
                        .nodes(List.of(
                                GraphNode.builder()
                                        .componentId("test-service-1")
                                        .name("test-span-1")
                                        .build(),
                                GraphNode.builder()
                                        .componentId("test-service-2")
                                        .name("test-span-2")
                                        .build()))
                        .edges(List.of(
                                CollatorGraphEdge.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(2)
                                        .timestamps(List.of(1_000L, 3_000L))
                                        .durations(List.of(1_000L, 3_000L))
                                        .build(),
                                CollatorGraphEdge.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .type(GraphEdgeTypeIds.TRACE)
                                        .sampleSize(2)
                                        .timestamps(List.of(2_000L, 4_000L))
                                        .durations(List.of(2_000L, 4_000L))
                                        .build()))
                        .build());
    }
    
    private CallGraphCollator createCallGraphCollator() {
        return new CallGraphCollator(
                new GenericGraphCollator(),
                new EdgeHelper()
        );
    }
}
