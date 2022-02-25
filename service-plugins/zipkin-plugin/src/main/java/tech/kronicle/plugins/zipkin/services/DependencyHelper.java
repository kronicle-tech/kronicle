package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kronicle.plugins.zipkin.models.CollatorComponentDependency;
import tech.kronicle.plugins.zipkin.models.NodesAndDependencies;
import tech.kronicle.plugins.zipkin.models.ObjectWithTimestamps;
import tech.kronicle.plugins.zipkin.models.TimestampsForDependency;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class DependencyHelper {

    private final DependencyDurationCalculator dependencyDurationCalculator;
    private final SubComponentDependencyTagFilter subComponentDependencyTagFilter;

    public <D> List<Integer> mergeRelatedIndexes(List<D> dependencies, Function<D, List<Integer>> relatedIndexGetter) {
        return dependencies.stream()
                .map(relatedIndexGetter)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public <T, R> List<R> getFlattenedListValuesFromObjects(List<T> objects, Function<T, List<R>> valueGetter) {
        return objects.stream()
                .map(valueGetter)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public <D extends ObjectWithTimestamps> TimestampsForDependency getTimestampsForDependency(List<D> duplicateDependencies) {
        List<Long> timestamps = duplicateDependencies.stream()
                .map(D::getTimestamps)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        if (timestamps.isEmpty()) {
            return new TimestampsForDependency(null, null);
        }
        return new TimestampsForDependency(epochMicrosecondsToLocalDateTime(timestamps.get(0)),
                epochMicrosecondsToLocalDateTime(timestamps.get(timestamps.size() - 1)));
    }

    public SummarySubComponentDependencies createSubComponentDependencies(
            NodesAndDependencies<SummarySubComponentDependencyNode, SummaryComponentDependency> nodesAndDependencies) {
        return new SummarySubComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies());
    }

    public SummaryComponentDependency mergeDuplicateDependencies(List<CollatorComponentDependency> duplicateDependencies) {
        TimestampsForDependency timestampsForDependency = getTimestampsForDependency(duplicateDependencies);
        CollatorComponentDependency firstDependency = duplicateDependencies.get(0);
        return new SummaryComponentDependency(firstDependency.getSourceIndex(), firstDependency.getTargetIndex(),
                mergeRelatedIndexes(duplicateDependencies, CollatorComponentDependency::getRelatedIndexes), false, duplicateDependencies.size(),
                timestampsForDependency.getStartTimestamp(), timestampsForDependency.getEndTimestamp(),
                dependencyDurationCalculator.calculateDependencyDuration(duplicateDependencies));
    }

    public SummarySubComponentDependencyNode createSubComponentDependencyNode(Span span) {
        return new SummarySubComponentDependencyNode(
                span.getLocalEndpoint().getServiceName(),
                span.getName(),
                subComponentDependencyTagFilter.filterAndSortTags(span));
    }

    private LocalDateTime epochMicrosecondsToLocalDateTime(Long epochMicroseconds) {
        if (isNull(epochMicroseconds)) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.EPOCH.plus(epochMicroseconds, ChronoUnit.MICROS), ZoneOffset.UTC);
    }
}
