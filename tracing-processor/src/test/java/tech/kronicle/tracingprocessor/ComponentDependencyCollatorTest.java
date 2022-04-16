package tech.kronicle.tracingprocessor;

import org.junit.jupiter.api.Test;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.constants.DependencyTypeIds;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.tracingprocessor.TracingTestHelper.createTrace;

public class ComponentDependencyCollatorTest {
    
    private final TracingTestHelper testHelper = new TracingTestHelper();
    private final ComponentDependencyCollator underTest = new ComponentDependencyCollator(
            new GenericDependencyCollator(),
            new DependencyHelper(new DependencyDurationCalculator())
    );

    @Test
    public void collateDependenciesWhenThereANoTracesShouldReturnNoDependencies() {
        // Given
        List<GenericTrace> traces = List.of();
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).isEmpty();
        assertThat(returnValue.getDependencies()).isEmpty();
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
        List<GenericTrace> traces = List.of(createTrace(trace1Span1));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)));
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
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace2Span1 = testHelper.spanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace2Span2 = testHelper.spanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .sourceName("test-service-4")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(null, 2, List.of(3), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(2, 3, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .sourceName("test-service-4")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1, 2, 3), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2, 3), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0, 3), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(2, 3, List.of(0, 1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .sourceName("test-service-3")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .sourceName("test-service-3")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1, 2), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(3), testHelper.getTimestamp(4), testHelper.createDuration(3_000, 4_000, 3_000, 4_000, 4_000, 4_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(1), testHelper.getTimestamp(3), testHelper.createDuration(1_000, 3_000, 1_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(4), testHelper.createDuration(2_000, 4_000, 2_000, 4_000, 4_000, 4_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace2Span1 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-1")
                .build();
        GenericSpan trace2Span2 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .sourceName("test-service-2")
                .build();
        List<GenericTrace> traces = List.of(
                createTrace(trace1Span1, trace1Span2),
                createTrace(trace2Span1, trace2Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(1), testHelper.getTimestamp(3), testHelper.createDuration(1_000, 3_000, 1_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(4), testHelper.createDuration(2_000, 4_000, 2_000, 4_000, 4_000, 4_000)));
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
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .sourceName("test-service-2")
                .build();
        GenericSpan trace1Span4 = testHelper.spanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(null, 1, List.of(0), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 0, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
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
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-1")
                .build();
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceAndAManualDependencyShouldReturnDependenciesForBothWithTheManualDependencyMarkedAsManual() {
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
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-3", "test-service-4"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(2, 3, List.of(), DependencyTypeIds.TRACE, null, null, true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceAnd2ManualDependenciesShouldDedupTheNormalAndManualDependencies() {
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
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span2, trace1Span3));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-2", "test-service-3"),
                createDependency("test-service-3", "test-service-4"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1, 2), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(2, 3, List.of(), DependencyTypeIds.TRACE, null, null, true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereIsAnInboundManualDependencyShouldCorrectlyHandleTheDirectionOfTheDependency() {
        // Given
        List<GenericTrace> traces = List.of();
        List<Dependency> dependencies = List.of(
                createDependency("test-service-2", "test-service-1"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereAre2IdenticalManualDependenciesShouldCorrectlyHandleThem() {
        // Given
        List<GenericTrace> traces = List.of();
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"),
                createDependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependency() {
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
        GenericSpan trace1Span3 = testHelper.spanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
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
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
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
                .name("test-span-3")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, null, null, testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, null, null, testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
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
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
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
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .sourceName("test-service-2")
                .build();
        // GenericSpan 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<GenericTrace> traces = List.of(createTrace(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                createDependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), DependencyTypeIds.TRACE, null, null, false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), null),
                new SummaryComponentDependency(0, 1, List.of(), DependencyTypeIds.TRACE, null, null, false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), null));
    }

    private Dependency createDependency(String sourceComponentId, String targetComponentId) {
        return new Dependency(
                sourceComponentId,
                targetComponentId,
                DependencyTypeIds.TRACE,
                null,
                null
        );
    }
}
