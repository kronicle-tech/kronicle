package tech.kronicle.tracingprocessor;

import lombok.Value;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.models.ObjectWithComponentId;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import tech.kronicle.sdk.models.SummaryDependencies;
import tech.kronicle.utils.MapCollectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class GenericDependencyCollator {

    public <N extends ObjectWithComponentId, D extends ObjectWithSourceIndexAndTargetIndex, S extends SummaryDependencies<N, D>> S createDependencies(
      List<GenericTrace> traces,
      Function<GenericSpan, N> createNode,
      Comparator<N> nodeComparator,
      Function<List<CollatorComponentDependency>, D> mergeDuplicateDependencies,
      SummaryDependenciesFactory<N, D, S> summaryDependenciesFactory
    ) {
        List<N> nodes = createNodes(traces, createNode, nodeComparator);
        Map<N, Integer> nodeMap = createNodeMap(nodes);

        List<D> dependencies = traces.stream()
                .flatMap(trace -> createDependencies(trace, nodeMap, createNode))
                .collect(Collectors.groupingBy(SourceIndexAndTargetIndex::new))
                .values().stream()
                .map(mergeDuplicateDependencies)
                .sorted(Comparator.comparing(D::getSourceIndex, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(D::getTargetIndex))
                .collect(Collectors.toList());

        return summaryDependenciesFactory.create(nodes, dependencies);
    }

    private <N extends ObjectWithComponentId> List<N> createNodes(List<GenericTrace> traces, Function<GenericSpan, N> createNode, Comparator<N> nodeComparator) {
        return traces.stream()
                .map(GenericTrace::getSpans)
                .flatMap(Collection::stream)
                .map(createNode)
                .distinct()
                .sorted(nodeComparator)
                .collect(Collectors.toList());
    }

    private <N extends ObjectWithComponentId> Map<N, Integer> createNodeMap(List<N> nodes) {
        return IntStream.range(0, nodes.size())
                .mapToObj(index -> Map.entry(nodes.get(index), index))
                .collect(MapCollectors.toMap());
    }

    private <N extends ObjectWithComponentId> Stream<CollatorComponentDependency> createDependencies(GenericTrace trace, Map<N, Integer> nodeMap,
            Function<GenericSpan, N> createNode) {
        List<CollatorComponentDependency> traceDependencies = trace.getSpans().stream()
                .map(span -> new SpanAndParentSpan(span, findParentSpan(trace, span.getParentId())))
                .map(spanAndParentSpan -> new CollatorComponentDependency(getSourceIndex(spanAndParentSpan, nodeMap, createNode),
                        getTargetIndex(spanAndParentSpan, nodeMap, createNode), null, spanAndParentSpan.span.getTimestamp(),
                        spanAndParentSpan.span.getDuration()))
                .filter(dependency -> !Objects.equals(dependency.getSourceIndex(), dependency.getTargetIndex()))
                .collect(Collectors.toList());
        List<Integer> relatedIndexes = traceDependencies.stream()
                .flatMap(dependency -> Stream.of(dependency.getSourceIndex(), dependency.getTargetIndex()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return traceDependencies.stream()
                .map(dependency -> dependency.withRelatedIndexes(getRelatedIndexesForDependency(relatedIndexes, dependency)));
    }

    private GenericSpan findParentSpan(GenericTrace trace, String parentId) {
        return trace.getSpans().stream()
                .filter(span -> Objects.equals(span.getId(), parentId))
                .findFirst().orElse(null);
    }

    private List<Integer> getRelatedIndexesForDependency(List<Integer> relatedIndexes, CollatorComponentDependency dependency) {
        List<Integer> newList = new ArrayList<>(relatedIndexes);
        newList.removeIf(relatedIndex -> Objects.equals(relatedIndex, dependency.getSourceIndex())
                || Objects.equals(relatedIndex, dependency.getTargetIndex()));
        newList.sort(Comparator.naturalOrder());
        return newList;
    }

    private <N extends ObjectWithComponentId> Integer getSourceIndex(SpanAndParentSpan spanAndParentSpan, Map<N, Integer> nodeMap, Function<GenericSpan, N> createNode) {
        return nonNull(spanAndParentSpan.parentSpan) ? getNodeIndex(spanAndParentSpan.parentSpan, nodeMap, createNode) : null;
    }

    private <N extends ObjectWithComponentId> int getTargetIndex(SpanAndParentSpan spanAndParentSpan, Map<N, Integer> nodeMap, Function<GenericSpan, N> createNode) {
        return getNodeIndex(spanAndParentSpan.span, nodeMap, createNode);
    }

    private <N extends ObjectWithComponentId> int getNodeIndex(GenericSpan span, Map<N, Integer> nodeMap, Function<GenericSpan, N> createNode) {
        return nodeMap.get(createNode.apply(span));
    }
    
    @Value
    private static class SpanAndParentSpan {

        GenericSpan span;
        GenericSpan parentSpan;
    }
}
