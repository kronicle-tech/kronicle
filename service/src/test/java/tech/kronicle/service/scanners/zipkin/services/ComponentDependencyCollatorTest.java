package tech.kronicle.service.scanners.zipkin.services;

import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.service.scanners.zipkin.models.api.Span;
import tech.kronicle.service.scanners.zipkin.spring.ZipkinConfiguration;
import tech.kronicle.service.scanners.zipkin.testutils.ZipkinApiModelTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDependencyCollatorTest {
    
    private final ZipkinApiModelTestHelper testHelper = new ZipkinApiModelTestHelper();
    private final ComponentDependencyCollator underTest = new ComponentDependencyCollator(new GenericDependencyCollator(), new ZipkinConfiguration().componentNodeComparator(),
            new DependencyHelper(new DependencyDurationCalculator(), new SubComponentDependencyTagFilter()));

    @Test
    public void collateDependenciesWhenThereANoTracesShouldReturnNoDependencies() {
        // Given
        List<List<Span>> traces = List.of();
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
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)));
    }

    @Test
    public void collateDependenciesWhenThereIs1TraceContaining2ConnectedSpansShouldReturn2Dependencies() {
        // Given
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
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
    }

    @Test
    public void collateDependenciesWhenThereAreMultipleTracesShouldReturnMultipleNoneConnectedDependencies() {
        // Given
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
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        Span trace2Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-4").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));
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
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(null, 2, List.of(3), false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(2, 3, List.of(), false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceContainingMoreThan1SpanShouldIncludeRelatedIds() {
        // Given
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
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-4").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
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
                new SummaryComponentDependency(null, 0, List.of(1, 2, 3), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2, 3), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0, 3), false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(2, 3, List.of(0, 1), false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesShouldDedupRelatedIdsForDuplicateDependencies() {
        // Given
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
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span2.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1, 2), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0), false, 2, testHelper.getTimestamp(3), testHelper.getTimestamp(4), testHelper.createDuration(3_000, 4_000, 3_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesShouldDedupDependenciesInTheSameTrace() {
        // Given
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
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 2, testHelper.getTimestamp(1), testHelper.getTimestamp(3), testHelper.createDuration(1_000, 3_000, 1_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(4), testHelper.createDuration(2_000, 4_000, 2_000, 4_000, 4_000, 4_000)));
    }
    
    @Test
    public void collateDependenciesShouldDedupDependenciesInSeparateTraces() {
        // Given
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
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace2Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace2Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 2, testHelper.getTimestamp(1), testHelper.getTimestamp(3), testHelper.createDuration(1_000, 3_000, 1_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(4), testHelper.createDuration(2_000, 4_000, 2_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesShouldNotDedupDependenciesInOppositeDirections() {
        // Given
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
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span3.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(null, 1, List.of(0), false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 0, List.of(), false, 1, testHelper.getTimestamp(4), testHelper.getTimestamp(4), testHelper.createDuration(4_000, 4_000, 4_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesShouldIgnoreSelfDependencies() {
        // Given
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
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceAndAManualDependencyShouldReturnDependenciesForBothWithTheManualDependencyMarkedAsManual() {
        // Given
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
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-3", "test-service-4"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(2, 3, List.of(), true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereIsATraceAnd2ManualDependenciesShouldDedupTheNormalAndManualDependencies() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .parentId(trace1Span2.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3));
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-2", "test-service-3"),
                new Dependency("test-service-3", "test-service-4"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-3"),
                new SummaryComponentDependencyNode("test-service-4"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1, 2), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(2), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)),
                new SummaryComponentDependency(1, 2, List.of(0), false, 1, testHelper.getTimestamp(3), testHelper.getTimestamp(3), testHelper.createDuration(3_000, 3_000, 3_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(2, 3, List.of(), true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereIsAnInboundManualDependencyShouldCorrectlyHandleTheDirectionOfTheDependency() {
        // Given
        List<List<Span>> traces = List.of();
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-2", "test-service-1"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-2"),
                new SummaryComponentDependencyNode("test-service-1"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesWhenThereAre2IdenticalManualDependenciesShouldCorrectlyHandleThem() {
        // Given
        List<List<Span>> traces = List.of();
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"),
                new Dependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(0, 1, List.of(), true, 0, null, null, null));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependency() {
        // Given
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
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of();

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependencyWhenOneOfTheSpansHasNoTimestamp() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependencyWhenAllOfTheSpansHaveNoTimestamps() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, null, null, testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, null, null, testHelper.createDuration(2_000, 3_000, 2_000, 3_000, 3_000, 3_000)));
    }

    @Test
    public void collateDependenciesShouldCalculateTheDurationStatsForADependencyWhenOneOfTheSpansHasNoDuration() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
    }

    @Test
    public void collateDependenciesShouldCalculateTheDurationStatsForADependencyWhenAllOfTheSpansHaveNoDurations() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));
        List<Dependency> dependencies = List.of(
                new Dependency("test-service-1", "test-service-2"));

        // When
        SummaryComponentDependencies returnValue = underTest.collateDependencies(traces, dependencies);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummaryComponentDependencyNode("test-service-1"),
                new SummaryComponentDependencyNode("test-service-2"));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), null),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), null));
    }
}
