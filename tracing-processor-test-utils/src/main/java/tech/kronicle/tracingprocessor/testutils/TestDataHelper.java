package tech.kronicle.tracingprocessor.testutils;

import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.constants.GraphEdgeTypeIds;
import tech.kronicle.sdk.models.Dependency;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestDataHelper {

    public static List<TracingData> createTracingDataList() {
        return createTracingDataList(2);
    }

    public static List<TracingData> createTracingDataList(int traceDataCount) {
        return createList(TestDataHelper::createTracingData, traceDataCount);
    }

    public static TracingData createTracingData() {
        return createTracingData(1);
    }

    public static TracingData createTracingData(int tracingDataNumber) {
        return createTracingData(tracingDataNumber, 2, 3);
    }

    public static TracingData createTracingData(int tracingDataNumber, int dependencyCount, int traceCount) {
        return TracingData.builder()
                .pluginId("test-plugin-id-" + tracingDataNumber)
                .environmentId("test-environment-id-" + tracingDataNumber)
                .id("test-tracing-data-id-" + tracingDataNumber)
                .name("Test Tracing Data " + tracingDataNumber)
                .dependencies(
                        createList(dependencyNumber -> createDependency(tracingDataNumber, dependencyNumber), dependencyCount)
                )
                .traces(
                        createList(traceNumber -> createTrace(tracingDataNumber, traceNumber, 2), traceCount)
                )
                .build();
    }

    public static TracingData createTracingData(List<GenericTrace> traces) {
        return TracingData.builder()
                .traces(traces)
                .build();
    }

    public static TracingData createTracingData(List<GenericTrace> traces, List<Dependency> dependencies) {
        return TracingData.builder()
                .traces(traces)
                .dependencies(dependencies)
                .build();
    }

    public static Dependency createDependency(int tracingDataNumber, int dependencyNumber) {
        return new Dependency(
                "test-source-" + tracingDataNumber + "-" + dependencyNumber,
                "test-target-" + tracingDataNumber + "-" + dependencyNumber,
                GraphEdgeTypeIds.TRACE,
                null,
                null
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

    private static <T> List<T> createList(IntFunction<T> createItem, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(createItem)
                .collect(Collectors.toList());
    }

    private TestDataHelper() {
    }
}
