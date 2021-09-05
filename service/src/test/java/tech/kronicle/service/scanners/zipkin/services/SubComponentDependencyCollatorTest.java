package tech.kronicle.service.scanners.zipkin.services;

import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;
import tech.kronicle.service.scanners.zipkin.models.api.Span;
import tech.kronicle.service.scanners.zipkin.spring.ZipkinConfiguration;
import tech.kronicle.service.scanners.zipkin.testutils.ZipkinApiModelTestHelper;
import tech.kronicle.service.services.MapComparator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SubComponentDependencyCollatorTest {

    private final ZipkinApiModelTestHelper testHelper = new ZipkinApiModelTestHelper();
    private final SubComponentDependencyCollator underTest = createSubComponentDependencyCollator();

    @Test
    public void collateDependenciesWhenThereIsNoTracesShouldReturnNoDependencies() {
        // Given
        List<List<Span>> traces = List.of();

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

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

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()));
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

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
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
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-4").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()),
                new SummarySubComponentDependencyNode("test-service-3", "test-span-3", Map.of()),
                new SummarySubComponentDependencyNode("test-service-4", "test-span-4", Map.of()));
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
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-4")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-4").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()),
                new SummarySubComponentDependencyNode("test-service-3", "test-span-3", Map.of()),
                new SummarySubComponentDependencyNode("test-service-4", "test-span-4", Map.of()));
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
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-3")
                .parentId(trace1Span2.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-3").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()),
                new SummarySubComponentDependencyNode("test-service-3", "test-span-3", Map.of()));
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
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
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
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        Span trace1Span4 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .parentId(trace1Span3.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2, trace1Span3, trace1Span4));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .name("test-span-1")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)));
    }

    @Test
    public void collateDependenciesShouldIncludeConfiguredTagsAndIgnoreOtherTags() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b",
                        "event.channelId", "test-value-1c",
                        "event.type", "test-value-1d",
                        "event.version", "test-value-1e",
                        "should.be.ignored", "test-value-1f"))
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .parentId(trace1Span1.getId())
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .tags(Map.of("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b",
                        "event.channelId", "test-value-2c",
                        "event.type", "test-value-2d",
                        "event.version", "test-value-2e",
                        "should.be.ignored", "test-value-2f"))
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1",
                        Map.of("http.path_template", "test-value-1a",
                                "event.organisationId", "test-value-1b",
                                "event.channelId", "test-value-1c",
                                "event.type", "test-value-1d",
                                "event.version", "test-value-1e")),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2",
                        Map.of("http.path_template", "test-value-2a",
                                "event.organisationId", "test-value-2b",
                                "event.channelId", "test-value-2c",
                                "event.type", "test-value-2d",
                                "event.version", "test-value-2e")));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
    }

    @Test
    public void collateDependenciesShouldNotDedupSpansThatVaryOnlyBySpanName() {
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
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-1", "test-span-2", Map.of()));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
    }

    @Test
    public void collateDependenciesShouldNotDedupSpansThatVaryOnlyByConfiguredTag() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-1"))
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-2"))
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of("http.path_template", "test-value-1")),
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of("http.path_template", "test-value-2")));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), testHelper.createDuration(1_000, 1_000, 1_000, 1_000, 1_000, 1_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 1, testHelper.getTimestamp(2), testHelper.getTimestamp(2), testHelper.createDuration(2_000, 2_000, 2_000, 2_000, 2_000, 2_000)));
    }

    @Test
    public void collateDependenciesShouldDedupSpansWithSameSpanNameAndSameConfiguredTags() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .tags(Map.of("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        Span trace2Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        Span trace2Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace2Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .tags(Map.of("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span2),
                List.of(trace2Span1, trace2Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1",
                        Map.of("http.path_template", "test-value-1a",
                                "event.organisationId", "test-value-1b")),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2",
                        Map.of("http.path_template", "test-value-2a",
                                "event.organisationId", "test-value-2b")));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 2, testHelper.getTimestamp(1), testHelper.getTimestamp(3), testHelper.createDuration(1_000, 3_000, 1_000, 3_000, 3_000, 3_000)),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(4), testHelper.createDuration(2_000, 4_000, 2_000, 4_000, 4_000, 4_000)));
    }

    @Test
    public void collateDependenciesShouldFindTheStartAndEndTimestampsForADependency() {
        // Given
        Span trace1Span1 = testHelper.createTestSpanBuilder()
                .name("test-span-1")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-1").build())
                .tags(Map.of("http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b"))
                .build();
        Span trace1Span2 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .tags(Map.of("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        Span trace1Span3 = testHelper.createTestSpanBuilder()
                .name("test-span-2")
                .parentId(trace1Span1.getId())
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .tags(Map.of("http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b"))
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of(
                        "http.path_template", "test-value-1a",
                        "event.organisationId", "test-value-1b")),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of(
                        "http.path_template", "test-value-2a",
                        "event.organisationId", "test-value-2b")));
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
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .name("test-span-2")
                .timestamp(null)
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(testHelper.getNextDurationInMicroseconds())
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
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
                .name("test-span-2")
                .timestamp(testHelper.getNextTimestampInMicroseconds())
                .duration(null)
                .parentId(trace1Span1.getId())
                .localEndpoint(testHelper.createTestEndpointBuilder().serviceName("test-service-2").build())
                .build();
        // Span 3 is deliberately before span 2 to check that start and end timestamps are calculated correctly
        List<List<Span>> traces = List.of(
                List.of(trace1Span1, trace1Span3, trace1Span2));

        // When
        SummarySubComponentDependencies returnValue = underTest.collateDependencies(traces);

        // Then
        assertThat(returnValue.getNodes()).containsExactly(
                new SummarySubComponentDependencyNode("test-service-1", "test-span-1", Map.of()),
                new SummarySubComponentDependencyNode("test-service-2", "test-span-2", Map.of()));
        assertThat(returnValue.getDependencies()).containsExactly(
                new SummaryComponentDependency(null, 0, List.of(1), false, 1, testHelper.getTimestamp(1), testHelper.getTimestamp(1), null),
                new SummaryComponentDependency(0, 1, List.of(), false, 2, testHelper.getTimestamp(2), testHelper.getTimestamp(3), null));
    }

    private SubComponentDependencyCollator createSubComponentDependencyCollator() {
        return new SubComponentDependencyCollator(new GenericDependencyCollator(), new ZipkinConfiguration().subComponentNodeComparator(new MapComparator<>()),
                new DependencyHelper(new DependencyDurationCalculator(), new SubComponentDependencyTagFilter()));
    }
}
