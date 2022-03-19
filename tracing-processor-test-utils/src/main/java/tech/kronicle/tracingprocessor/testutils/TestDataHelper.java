package tech.kronicle.tracingprocessor.testutils;

import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;
import tech.kronicle.tracingprocessor.ProcessedTracingData;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestDataHelper {

    public static List<TracingData> createTracingDataList() {
        return createTracingDataList(2);
    }

    public static List<TracingData> createTracingDataList(int traceDataCount) {
        return createList(tracingDataNumber -> createTracingData(tracingDataNumber), traceDataCount);
    }

    public static TracingData createTracingData() {
        return createTracingData(1);
    }

    public static TracingData createTracingData(int tracingDataNumber) {
        return createTracingData(tracingDataNumber, 2, 3);
    }

    public static TracingData createTracingData(int tracingDataNumber, int dependencyCount, int traceCount) {
        return TracingData.builder()
                .dependencies(
                        createList(dependencyNumber -> createDependency(tracingDataNumber, dependencyNumber), dependencyCount)
                )
                .traces(
                        createList(traceNumber -> createTrace(tracingDataNumber, traceNumber, 2), traceCount)
                )
                .build();
    }

    public static Dependency createDependency(int tracingDataNumber, int dependencyNumber) {
        return new Dependency(
                "test-source-" + tracingDataNumber + "-" + dependencyNumber,
                "test-target-" + tracingDataNumber + "-" + dependencyNumber
        );
    }

    public static GenericTrace createTrace(int tracingDataNumber, int traceNumber, int spanCount) {
        return GenericTrace.builder()
                .spans(createList(spanNumber -> createSpan(tracingDataNumber, traceNumber, spanNumber), spanCount))
                .build();
    }

    public static GenericSpan createSpan(int tracingDataNumber, int traceNumber, int spanNumber) {
        return GenericSpan.builder()
                .sourceName("test-source-" + tracingDataNumber + "-" + traceNumber + "-" + spanNumber)
                .build();
    }

    public static ProcessedTracingData createProcessedTracingData() {
        return ProcessedTracingData.builder()
                .componentDependencies(createComponentDependencies())
                .subComponentDependencies(createSubComponentDependencies())
                .callGraphs(createCallGraphs())
                .build();
    }

    public static SummaryComponentDependencies createComponentDependencies() {
        return createComponentDependencies(3, 2);
    }

    public static SummaryComponentDependencies createComponentDependencies(int nodeCount, int dependencyCount) {
        return SummaryComponentDependencies.builder()
                .nodes(List.of(
                        createComponentDependencyNode(1),
                        createComponentDependencyNode(2)
                ))
                .dependencies(List.of(
                        createComponentDependency(1),
                        createComponentDependency(2)
                ))
                .build();
    }

    public static SummaryComponentDependencyNode createComponentDependencyNode(int nodeNumber) {
        return SummaryComponentDependencyNode.builder()
                .componentId("test-component-id-" + nodeNumber)
                .build();
    }

    public static SummarySubComponentDependencies createSubComponentDependencies() {
        return SummarySubComponentDependencies.builder()
                .nodes(
                        createList(nodeNumber -> createSubComponentDependencyNode(nodeNumber), 3)
                )
                .dependencies(
                        createList(componentDependencyNumber -> createComponentDependency(componentDependencyNumber), 2)
                )
                .build();
    }

    public static SummarySubComponentDependencyNode createSubComponentDependencyNode(int nodeNumber) {
        return SummarySubComponentDependencyNode.builder()
                .componentId("test-component-id-" + nodeNumber)
                .build();
    }

    public static SummaryComponentDependency createComponentDependency(int componentDependencyNumber) {
        return SummaryComponentDependency.builder()
                .sourceIndex(100 + componentDependencyNumber)
                .targetIndex(200 + componentDependencyNumber)
                .build();
    }

    public static List<SummaryCallGraph> createCallGraphs() {
        return createList(callGraphNumber -> createCallGraph(callGraphNumber), 2);
    }

    public static SummaryCallGraph createCallGraph(int callGraphNumber) {
        int callGraphUniqueNumber = callGraphNumber * 100;
        return SummaryCallGraph.builder()
                .nodes(List.of(
                        createSubComponentDependencyNode(callGraphUniqueNumber + 1),
                        createSubComponentDependencyNode(callGraphUniqueNumber + 2)
                ))
                .dependencies(List.of(
                        createComponentDependency(callGraphUniqueNumber + 1),
                        createComponentDependency(callGraphUniqueNumber + 2)
                ))
                .traceCount(callGraphNumber)
                .build();
    }

    private static <T> List<T> createList(IntFunction<T> createItem, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(createItem)
                .collect(Collectors.toList());
    }

    private TestDataHelper() {
    }
}
