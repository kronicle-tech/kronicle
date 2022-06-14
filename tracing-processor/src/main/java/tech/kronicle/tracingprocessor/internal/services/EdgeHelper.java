package tech.kronicle.tracingprocessor.internal.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTag;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.sdk.models.Tag;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;
import tech.kronicle.tracingprocessor.internal.models.TimestampsForEdge;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class EdgeHelper {

    public <D> List<Integer> mergeRelatedIndexes(List<D> edges, Function<D, List<Integer>> relatedIndexGetter) {
        return edges.stream()
                .map(relatedIndexGetter)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public TimestampsForEdge getTimestampsForEdge(List<Long> timestamps) {
        if (timestamps.isEmpty()) {
            return new TimestampsForEdge(null, null);
        }
        return new TimestampsForEdge(
                epochMicrosecondsToLocalDateTime(timestamps.get(0)),
                epochMicrosecondsToLocalDateTime(timestamps.get(timestamps.size() - 1))
        );
    }

    public CollatorGraphEdge mergeDuplicateEdges(List<CollatorGraphEdge> duplicateEdges) {
        CollatorGraphEdge firstEdge = duplicateEdges.get(0);
        return new CollatorGraphEdge(
                firstEdge.getSourceIndex(),
                firstEdge.getTargetIndex(),
                mergeRelatedIndexes(duplicateEdges, CollatorGraphEdge::getRelatedIndexes),
                firstEdge.getType(),
                firstEdge.getLabel(),
                firstEdge.getDescription(),
                getSampleSize(duplicateEdges),
                getTimestamps(duplicateEdges),
                getDurations(duplicateEdges)
        );
    }

    private Integer getSampleSize(List<CollatorGraphEdge> edges) {
        return edges.stream()
                .map(CollatorGraphEdge::getSampleSize)
                .mapToInt(sampleSize -> nonNull(sampleSize) ? sampleSize : 1)
                .sum();
    }

    private List<Long> getTimestamps(List<CollatorGraphEdge> edges) {
        return edges.stream()
                .map(CollatorGraphEdge::getTimestamps)
                .flatMap(Collection::stream)
                .sorted()
                .collect(toUnmodifiableList());
    }

    private List<Long> getDurations(List<CollatorGraphEdge> edges) {
        return edges.stream()
                .map(CollatorGraphEdge::getDurations)
                .flatMap(Collection::stream)
                .sorted()
                .collect(toUnmodifiableList());
    }

    public GraphNode createSubComponentNode(GenericSpan span) {
        return GraphNode.builder()
                .componentId(span.getSourceName())
                .name(span.getName())
                .tags(mapTags(span.getSubComponentTags()))
                .build();
    }

    private List<Tag> mapTags(List<GenericTag> tags) {
        return tags.stream()
                .map(tag -> new Tag(tag.getKey(), tag.getValue()))
                .collect(toUnmodifiableList());
    }

    private LocalDateTime epochMicrosecondsToLocalDateTime(Long epochMicroseconds) {
        if (isNull(epochMicroseconds)) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.EPOCH.plus(epochMicroseconds, ChronoUnit.MICROS), ZoneOffset.UTC);
    }
}
