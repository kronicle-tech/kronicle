package tech.kronicle.tracingprocessor.internal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class ComponentGraphCollator {

    private final GenericGraphCollator genericGraphCollator;
    private final EdgeHelper edgeHelper;
    
    public CollatorGraph collateGraph(TracingData tracingData) {
        CollatorGraph graph = genericGraphCollator.createGraph(
                tracingData.getTraces(),
                this::createNode,
                NodeComparators.COMPONENT_NODE_COMPARATOR,
                edgeHelper::mergeDuplicateEdges
        );
        return addDependencies(
                graph.getNodes(),
                graph.getEdges(),
                tracingData.getDependencies()
        );
    }

    private GraphNode createNode(GenericSpan span) {
        return GraphNode.builder()
                .componentId(span.getSourceName())
                .build();
    }
    
    private CollatorGraph addDependencies(
            List<GraphNode> nodes,
            List<CollatorGraphEdge> edges,
            List<Dependency> dependencies
    ) {
        Map<Boolean, List<Dependency>> groupedDependencies = dependencies.stream()
                .distinct()
                .collect(Collectors.groupingBy(
                        dependency -> dependencyAlreadyExistsAsEdge(dependency, nodes, edges)
                ));

        List<Dependency> newDependencies = groupedDependencies.get(false);

        if (isNull(newDependencies)) {
            return CollatorGraph.builder()
                    .nodes(nodes)
                    .edges(edges)
                    .build();
        } else {
            List<GraphNode> allNodes = new ArrayList<>(nodes);
            List<CollatorGraphEdge> allEdges = new ArrayList<>(edges);
            allEdges.addAll(createEdgesForDependencies(allNodes, newDependencies));
            return CollatorGraph.builder()
                    .nodes(allNodes)
                    .edges(allEdges)
                    .build();
        }
    }

    private List<CollatorGraphEdge> createEdgesForDependencies(
            List<GraphNode> nodes,
            List<Dependency> dependencies
    ) {
        return dependencies.stream()
                .map(dependency -> createEdgeForDependency(nodes, dependency))
                .collect(toUnmodifiableList());
    }

    private CollatorGraphEdge createEdgeForDependency(List<GraphNode> nodes, Dependency dependency) {
        return new CollatorGraphEdge(
                getOrAddNode(nodes, dependency.getSourceComponentId()),
                getOrAddNode(nodes, dependency.getTargetComponentId()),
                List.of(),
                dependency.getTypeId(),
                dependency.getLabel(),
                dependency.getDescription(),
                0,
                null,
                null
        );
    }

    private int getOrAddNode(List<GraphNode> nodes, String componentId) {
        OptionalInt nodeIndexMatch = IntStream.range(0, nodes.size())
                .filter(nodeIndex -> Objects.equals(nodes.get(nodeIndex).getComponentId(), componentId))
                .findFirst();

        if (nodeIndexMatch.isPresent()) {
            return nodeIndexMatch.getAsInt();
        }

        nodes.add(GraphNode.builder().componentId(componentId).build());
        return nodes.size() - 1;
    }

    private boolean dependencyAlreadyExistsAsEdge(
            Dependency dependency,
            List<GraphNode> nodes,
            List<CollatorGraphEdge> edges
    ) {
        return edges.stream()
                .anyMatch(edge -> nodeComponentIdEqualsComponentId(nodes, edge.getSourceIndex(), dependency.getSourceComponentId())
                        && nodeComponentIdEqualsComponentId(nodes, edge.getTargetIndex(), dependency.getTargetComponentId()));
    }

    private boolean nodeComponentIdEqualsComponentId(List<GraphNode> nodes, Integer index, String componentId) {
        return nonNull(index) && Objects.equals(getNodeComponentIdByIndex(nodes, index), componentId);
    }

    private String getNodeComponentIdByIndex(List<GraphNode> nodes, int index) {
        return nodes.get(index).getComponentId();
    }
}
