package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.tracingprocessor.TracingTestHelper.createTrace;

public class CallGraphCollatorTest {

    private final TracingTestHelper testHelper = new TracingTestHelper();
    private final CallGraphCollator underTest = createCallGraphCollator();

    @Test
    public void collateCallGraphsShouldReturnNoCallGraphsWhenThereAreNoTraces() {
        // Given
        List<GenericTrace> traces = List.of();

        // When
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

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
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).containsExactly(SummaryCallGraph.builder()
                .traceCount(1)
                .nodes(List.of(
                        SummarySubComponentDependencyNode.builder()
                                .componentId("test-service-1")
                                .spanName("test-span-1")
                                .build()))
                .dependencies(List.of(
                        SummaryComponentDependency.builder()
                                .targetIndex(0)
                                .typeId(DependencyTypeIds.TRACE)
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(1))
                                .endTimestamp(testHelper.getTimestamp(1))
                                .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
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
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).containsExactly(SummaryCallGraph.builder()
                .traceCount(1)
                .nodes(List.of(
                        SummarySubComponentDependencyNode.builder()
                                .componentId("test-service-1")
                                .spanName("test-span-1")
                                .build(),
                        SummarySubComponentDependencyNode.builder()
                                .componentId("test-service-2")
                                .spanName("test-span-2")
                                .build()))
                .dependencies(List.of(
                        SummaryComponentDependency.builder()
                                .targetIndex(0)
                                .relatedIndexes(List.of(1))
                                .typeId(DependencyTypeIds.TRACE)
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(1))
                                .endTimestamp(testHelper.getTimestamp(1))
                                .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                .build(),
                        SummaryComponentDependency.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
                                .typeId(DependencyTypeIds.TRACE)
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(2))
                                .endTimestamp(testHelper.getTimestamp(2))
                                .duration(testHelper.createDuration(2000L, 2000L, 2000L, 2000L, 2000L, 2000L))
                                .build()))
                .build());
    }

    @Test
    public void collateCallGraphsShouldMergeDuplicateDependenciesInTheSameTraceAndSetSampleSizeToIncludeAllDuplicates() {
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
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).containsExactly(SummaryCallGraph.builder()
                .traceCount(1)
                .nodes(List.of(
                        SummarySubComponentDependencyNode.builder()
                                .componentId("test-service-1")
                                .spanName("test-span-1")
                                .build(),
                        SummarySubComponentDependencyNode.builder()
                                .componentId("test-service-2")
                                .spanName("test-span-2")
                                .build()))
                .dependencies(List.of(
                        SummaryComponentDependency.builder()
                                .targetIndex(0)
                                .relatedIndexes(List.of(1))
                                .typeId(DependencyTypeIds.TRACE)
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(1))
                                .endTimestamp(testHelper.getTimestamp(1))
                                .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                .build(),
                        SummaryComponentDependency.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
                                .typeId(DependencyTypeIds.TRACE)
                                .manual(false)
                                .sampleSize(2)
                                .startTimestamp(testHelper.getTimestamp(2))
                                .endTimestamp(testHelper.getTimestamp(3))
                                .duration(testHelper.createDuration(2000L, 3000L, 2000L, 3000L, 3000L, 3000L))
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
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).containsExactly(
                SummaryCallGraph.builder()
                        .traceCount(1)
                        .nodes(List.of(
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-3")
                                        .spanName("test-span-3")
                                        .build(),
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-4")
                                        .spanName("test-span-4")
                                        .build()))
                        .dependencies(List.of(
                                SummaryComponentDependency.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(3))
                                        .endTimestamp(testHelper.getTimestamp(3))
                                        .duration(testHelper.createDuration(3000L, 3000L, 3000L, 3000L, 3000L, 3000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(4))
                                        .endTimestamp(testHelper.getTimestamp(4))
                                        .duration(testHelper.createDuration(4000L, 4000L, 4000L, 4000L, 4000L, 4000L))
                                        .build()))
                        .build(),
                SummaryCallGraph.builder()
                        .traceCount(1)
                        .nodes(List.of(
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-1")
                                        .spanName("test-span-1")
                                        .build(),
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-2")
                                        .spanName("test-span-2")
                                        .build()))
                        .dependencies(List.of(
                                SummaryComponentDependency.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(1))
                                        .endTimestamp(testHelper.getTimestamp(1))
                                        .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(2))
                                        .endTimestamp(testHelper.getTimestamp(2))
                                        .duration(testHelper.createDuration(2000L, 2000L, 2000L, 2000L, 2000L, 2000L))
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
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).containsExactly(
                SummaryCallGraph.builder()
                        .traceCount(2)
                        .nodes(List.of(
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-1")
                                        .spanName("test-span-1")
                                        .build(),
                                SummarySubComponentDependencyNode.builder()
                                        .componentId("test-service-2")
                                        .spanName("test-span-2")
                                        .build()))
                        .dependencies(List.of(
                                SummaryComponentDependency.builder()
                                        .targetIndex(0)
                                        .relatedIndexes(List.of(1))
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(2)
                                        .startTimestamp(testHelper.getTimestamp(1))
                                        .endTimestamp(testHelper.getTimestamp(3))
                                        .duration(testHelper.createDuration(1000L, 3000L, 1000L, 3000L, 3000L, 3000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
                                        .typeId(DependencyTypeIds.TRACE)
                                        .manual(false)
                                        .sampleSize(2)
                                        .startTimestamp(testHelper.getTimestamp(2))
                                        .endTimestamp(testHelper.getTimestamp(4))
                                        .duration(testHelper.createDuration(2000L, 4000L, 2000L, 4000L, 4000L, 4000L))
                                        .build()))
                        .build());
    }

    private CallGraphCollator createCallGraphCollator() {
        DependencyDurationCalculator dependencyDurationCalculator = new DependencyDurationCalculator();
        return new CallGraphCollator(
                new GenericDependencyCollator(),
                new DependencyHelper(dependencyDurationCalculator),
                dependencyDurationCalculator
        );
    }
}
