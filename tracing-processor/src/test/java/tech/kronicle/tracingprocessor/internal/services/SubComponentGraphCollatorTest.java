package tech.kronicle.tracingprocessor.internal.services;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTag;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.constants.GraphEdgeTypeIds;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.sdk.models.Tag;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.tracingprocessor.internal.services.TracingTestHelper.createTrace;
import static tech.kronicle.tracingprocessor.testutils.TestDataHelper.createTracingData;

public class SubComponentGraphCollatorTest {

    private final TracingTestHelper testHelper = new TracingTestHelper();
    private final SubComponentGraphCollator underTest = createSubComponentDependencyCollator();

    @Test
    public void collateDependenciesWhenThereIsNoTracesShouldReturnNoDependencies() {
        // Given
        List<GenericTrace> traces = List.of();

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).isEmpty();
        assertThat(returnValue.getEdges()).isEmpty();
    }

    @Test
    public void collateDependenciesWhenThereIs1TraceContaining1SpanShouldReturn1NodeAnd1Dependency() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)));
    }

    @Test
    public void collateDependenciesWhenThereIs1TraceContaining2ConnectedSpansShouldReturn2Dependencies() {
        // Given
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
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)));
    }

    @Test
    public void collateDependenciesWhenThereAreMultipleTracesShouldReturnMultipleNoneConnectedDependencies() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
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
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-4")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()),
                new GraphNode("test-service-3", "test-span-3", List.of()),
                new GraphNode("test-service-4", "test-span-4", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(null, 2, List.of(3), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(3_000L), List.of(3_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)),
                new CollatorGraphEdge(2, 3, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(4_000L), List.of(4_000L)));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceContainingMoreThan1SpanShouldIncludeRelatedIds() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .parentId(trace1Span2.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-4")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-4")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()),
                new GraphNode("test-service-3", "test-span-3", List.of()),
                new GraphNode("test-service-4", "test-span-4", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1, 2, 3), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(2, 3), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)),
                new CollatorGraphEdge(1, 2, List.of(0, 3), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(3_000L), List.of(3_000L)),
                new CollatorGraphEdge(2, 3, List.of(0, 1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(4_000L), List.of(4_000L)));
    }

    @Test
    public void collateDependenciesShouldDedupRelatedIdsForDuplicateDependencies() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .parentId(trace1Span2.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-3")
                .parentId(trace1Span2.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-3")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()),
                new GraphNode("test-service-3", "test-span-3", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1, 2), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(2), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)),
                new CollatorGraphEdge(1, 2, List.of(0), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(3_000L, 4_000L), List.of(3_000L, 4_000L)));
    }

    @Test
    public void collateDependenciesShouldDedupDependenciesInTheSameTrace() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(1_000L, 3_000L), List.of(1_000L, 3_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 4_000L), List.of(2_000L, 4_000L)));
    }

    @Test
    public void collateDependenciesShouldDedupDependenciesInSeparateTraces() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
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
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(1_000L, 3_000L), List.of(1_000L, 3_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 4_000L), List.of(2_000L, 4_000L)));
    }

    @Test
    public void collateDependenciesShouldNotDedupDependenciesInOppositeDirections() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-1")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(null, 1, List.of(0), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(3_000L), List.of(3_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)),
                new CollatorGraphEdge(1, 0, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(4_000L), List.of(4_000L)));
    }

    @Test
    public void collateDependenciesShouldIgnoreSelfDependencies() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-1")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2)
        );

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)));
    }

    @Test
    public void collateDependenciesShouldNotDedupSpansThatVaryOnlyBySpanName() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-1", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)));
    }

    @Test
    public void collateDependenciesShouldNotDedupSpansThatVaryOnlyByConfiguredTag() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .subComponentTags(createTags("http.path_template", "test-value-1"))
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-1")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .subComponentTags(createTags("http.path_template", "test-value-2"))
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of(new Tag("http.path_template", "test-value-1"))),
                new GraphNode("test-service-1", "test-span-1", List.of(new Tag("http.path_template", "test-value-2"))));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(2_000L), List.of(2_000L)));
    }
    
    @Test
    public void collateDependenciesShouldDedupSpansWithSameSpanNameAndSameConfiguredTags() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .subComponentTags(createTags("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .subComponentTags(createTags("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        GenericSpan trace2Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .subComponentTags(createTags("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        GenericSpan trace2Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .subComponentTags(createTags("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode(
                        "test-service-1",
                        "test-span-1",
                        List.of(
                                new Tag("event.organisationId", "test-value-1b"),
                                new Tag("http.path_template", "test-value-1a")
                        )
                ),
                new GraphNode(
                        "test-service-2",
                        "test-span-2",
                        List.of(
                                new Tag("event.organisationId", "test-value-2b"),
                                new Tag("http.path_template", "test-value-2a")
                        )
                )
        );
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(1_000L, 3_000L), List.of(1_000L, 3_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 4_000L), List.of(2_000L, 4_000L)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependency() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .subComponentTags(createTags("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .subComponentTags(createTags("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .subComponentTags(createTags("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode(
                        "test-service-1",
                        "test-span-1",
                        List.of(
                                new Tag("event.organisationId", "test-value-1b"),
                                new Tag("http.path_template", "test-value-1a")
                        )
                ),
                new GraphNode(
                        "test-service-2",
                        "test-span-2",
                        List.of(
                                new Tag("event.organisationId", "test-value-2b"),
                                new Tag("http.path_template", "test-value-2a")
                        )
                )
        );
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 3_000L), List.of(2_000L, 3_000L)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependencyWhenOneOfTheSpansHasNoTimestamp() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L), List.of(2_000L, 3_000L)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependencyWhenAllOfTheSpansHaveNoTimestamps() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, null, List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, null, List.of(2_000L, 3_000L)));
    }

    @Test
    public void collateDependenciesShouldCalculateTheDurationStatsForADependencyWhenOneOfTheSpansHasNoDuration() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), List.of(1_000L)),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 3_000L), List.of(2_000L)));
    }

    @Test
    public void collateDependenciesShouldCalculateTheDurationStatsForADependencyWhenAllOfTheSpansHaveNoDurations() {
        // Given
        GenericSpan trace1Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));

        // When
        CollatorGraph returnValue = underTest.collateGraph(createTracingData(traces));

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new GraphNode("test-service-1", "test-span-1", List.of()),
                new GraphNode("test-service-2", "test-span-2", List.of()));
        assertThat(returnValue.getEdges()).containsExactly(
                new CollatorGraphEdge(null, 0, List.of(1), GraphEdgeTypeIds.TRACE, null, null, 1, List.of(1_000L), null),
                new CollatorGraphEdge(0, 1, List.of(), GraphEdgeTypeIds.TRACE, null, null, 2, List.of(2_000L, 3_000L), null));
    }

    private List<GenericTag> createTags(String... args) {
        List<GenericTag> tags = new ArrayList<>();
        for (int index = 0; index < args.length; index += 2) {
            tags.add(new GenericTag(args[index], args[index + 1]));
        }
        return tags;
    }

    private SubComponentGraphCollator createSubComponentDependencyCollator() {
        return new SubComponentGraphCollator(
                new GenericGraphCollator(),
                new EdgeHelper()
        );
    }
}
