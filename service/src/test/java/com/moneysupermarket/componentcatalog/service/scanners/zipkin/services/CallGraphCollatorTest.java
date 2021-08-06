package com.moneysupermarket.componentcatalog.service.scanners.zipkin.services;

import com.moneysupermarket.componentcatalog.sdk.models.SummaryCallGraph;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependency;
import com.moneysupermarket.componentcatalog.sdk.models.SummarySubComponentDependencyNode;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Span;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.spring.ZipkinConfiguration;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.testutils.ZipkinApiModelTestHelper;
import com.moneysupermarket.componentcatalog.service.services.MapComparator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CallGraphCollatorTest {

    private final ZipkinApiModelTestHelper testHelper = new ZipkinApiModelTestHelper();
    private final CallGraphCollator underTest = createCallGraphCollator();

    @Test
    public void collateCallGraphsShouldReturnNoCallGraphsWhenThereAreNoTraces() {
        // Given
        List<List<Span>> traces = List.of();

        // When
        List<SummaryCallGraph> returnValue = underTest.collateCallGraphs(traces);

        // Then
        assertThat(returnValue).isEmpty();
    }

    @Test
    public void collateCallGraphsShouldReturnACallGraphWhenThereIsASingleTraceWithOneSpan() {
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1));

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
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));

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
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(1))
                                .endTimestamp(testHelper.getTimestamp(1))
                                .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                .build(),
                        SummaryComponentDependency.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
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
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2a = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span2b = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2a, trace1Span2b));

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
                                .manual(false)
                                .sampleSize(1)
                                .startTimestamp(testHelper.getTimestamp(1))
                                .endTimestamp(testHelper.getTimestamp(1))
                                .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                .build(),
                        SummaryComponentDependency.builder()
                                .sourceIndex(0)
                                .targetIndex(1)
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
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace2Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        Span trace2Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-4").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));

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
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(3))
                                        .endTimestamp(testHelper.getTimestamp(3))
                                        .duration(testHelper.createDuration(3000L, 3000L, 3000L, 3000L, 3000L, 3000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
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
                                        .manual(false)
                                        .sampleSize(1)
                                        .startTimestamp(testHelper.getTimestamp(1))
                                        .endTimestamp(testHelper.getTimestamp(1))
                                        .duration(testHelper.createDuration(1000L, 1000L, 1000L, 1000L, 1000L, 1000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
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
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace2Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace2Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));

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
                                        .manual(false)
                                        .sampleSize(2)
                                        .startTimestamp(testHelper.getTimestamp(1))
                                        .endTimestamp(testHelper.getTimestamp(3))
                                        .duration(testHelper.createDuration(1000L, 3000L, 1000L, 3000L, 3000L, 3000L))
                                        .build(),
                                SummaryComponentDependency.builder()
                                        .sourceIndex(0)
                                        .targetIndex(1)
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
        return new CallGraphCollator(new GenericDependencyCollator(), new ZipkinConfiguration().subComponentNodeComparator(new MapComparator<>()),
                new DependencyHelper(dependencyDurationCalculator, new SubComponentDependencyTagFilter()), dependencyDurationCalculator);
    }
}
