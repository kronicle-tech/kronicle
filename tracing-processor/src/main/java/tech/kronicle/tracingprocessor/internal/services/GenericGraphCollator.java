package tech.kronicle.tracingprocessor.internal.services;

import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.constants.GraphEdgeTypeIds;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdgeIdentity;
import tech.kronicle.tracingprocessor.internal.models.SpanAndParentSpan;
import tech.kronicle.utils.MapCollectors;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class GenericGraphCollator {

    public CollatorGraph createGraph(
      List<GenericTrace> traces,
      Function<GenericSpan, GraphNode> createNode,
      Comparator<GraphNode> nodeComparator,
      Function<List<CollatorGraphEdge>, CollatorGraphEdge> mergeDuplicateEdges
    ) {
        List<GraphNode> nodes = createNodes(traces, createNode, nodeComparator);
        Map<GraphNode, Integer> nodeMap = createNodeMap(nodes);

        List<CollatorGraphEdge> edges = traces.stream()
                .flatMap(trace -> createEdges(trace, nodeMap, createNode))
                .collect(Collectors.groupingBy(CollatorGraphEdgeIdentity::new))
                .values().stream()
                .map(mergeDuplicateEdges)
                .sorted(Comparator.comparing(CollatorGraphEdge::getSourceIndex, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(CollatorGraphEdge::getTargetIndex))
                .collect(Collectors.toList());

        return CollatorGraph.builder()
                .nodes(nodes)
                .edges(edges)
                .build();
    }

    private List<GraphNode> createNodes(List<GenericTrace> traces, Function<GenericSpan, GraphNode> createNode, Comparator<GraphNode> nodeComparator) {
        return traces.stream()
                .map(GenericTrace::getSpans)
                .flatMap(Collection::stream)
                .map(createNode)
                .distinct()
                .sorted(nodeComparator)
                .collect(Collectors.toList());
    }

    private Map<GraphNode, Integer> createNodeMap(List<GraphNode> nodes) {
        return IntStream.range(0, nodes.size())
                .mapToObj(index -> Map.entry(nodes.get(index), index))
                .collect(MapCollectors.toMap());
    }

    private Stream<CollatorGraphEdge> createEdges(GenericTrace trace, Map<GraphNode, Integer> nodeMap,
            Function<GenericSpan, GraphNode> createNode) {
        List<CollatorGraphEdge> traceEdges = trace.getSpans().stream()
                .map(span -> new SpanAndParentSpan(span, findParentSpan(trace, span.getParentId())))
                .map(spanAndParentSpan -> new CollatorGraphEdge(
                        getSourceIndex(spanAndParentSpan, nodeMap, createNode),
                        getTargetIndex(spanAndParentSpan, nodeMap, createNode),
                        null,
                        GraphEdgeTypeIds.TRACE,
                        null,
                        null,
                        null,
                        toListRemovingNull(spanAndParentSpan.getSpan().getTimestamp()),
                        toListRemovingNull(spanAndParentSpan.getSpan().getDuration())
                ))
                .filter(edge -> !Objects.equals(edge.getSourceIndex(), edge.getTargetIndex()))
                .collect(Collectors.toList());
        List<Integer> relatedIndexes = traceEdges.stream()
                .flatMap(edge -> Stream.of(edge.getSourceIndex(), edge.getTargetIndex()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return traceEdges.stream()
                .map(edge -> edge.withRelatedIndexes(getRelatedIndexesForEdge(relatedIndexes, edge)));
    }

    private List<Long> toListRemovingNull(Long value) {
        return nonNull(value) ? List.of(value) : List.of();
    }

    private GenericSpan findParentSpan(GenericTrace trace, String parentId) {
        return trace.getSpans().stream()
                .filter(span -> Objects.equals(span.getId(), parentId))
                .findFirst().orElse(null);
    }

    private List<Integer> getRelatedIndexesForEdge(List<Integer> relatedIndexes, CollatorGraphEdge edge) {
        List<Integer> newList = new ArrayList<>(relatedIndexes);
        newList.removeIf(relatedIndex -> Objects.equals(relatedIndex, edge.getSourceIndex())
                || Objects.equals(relatedIndex, edge.getTargetIndex()));
        newList.sort(Comparator.naturalOrder());
        return newList;
    }

    private Integer getSourceIndex(
            SpanAndParentSpan spanAndParentSpan, 
            Map<GraphNode, Integer> nodeMap, 
            Function<GenericSpan, GraphNode> createNode
    ) {
        return nonNull(spanAndParentSpan.getParentSpan()) ? 
                getNodeIndex(spanAndParentSpan.getParentSpan(), nodeMap, createNode) : 
                null;
    }

    private int getTargetIndex(
            SpanAndParentSpan spanAndParentSpan, 
            Map<GraphNode, Integer> nodeMap, 
            Function<GenericSpan, GraphNode> createNode
    ) {
        return getNodeIndex(spanAndParentSpan.getSpan(), nodeMap, createNode);
    }

    private int getNodeIndex(GenericSpan span, Map<GraphNode, Integer> nodeMap, Function<GenericSpan, GraphNode> createNode) {
        return nodeMap.get(createNode.apply(span));
    }
}
