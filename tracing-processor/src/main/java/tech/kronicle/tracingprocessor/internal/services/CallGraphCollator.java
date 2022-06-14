package tech.kronicle.tracingprocessor.internal.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphIdentity;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CallGraphCollator {

    private final GenericGraphCollator genericGraphCollator;
    private final EdgeHelper edgeHelper;

    public List<CollatorGraph> collateCallGraphs(TracingData tracingData) {
        return tracingData.getTraces().stream()
                .map(trace -> genericGraphCollator.createGraph(
                        List.of(trace),
                        edgeHelper::createSubComponentNode,
                        NodeComparators.SUB_COMPONENT_NODE_COMPARATOR,
                        edgeHelper::mergeDuplicateEdges
                ))
                .collect(Collectors.groupingBy(CollatorGraphIdentity::fromCollatorGraph))
                .values()
                .stream()
                .map(this::mergeDuplicateGraphs)
                .collect(toUnmodifiableList());
    }

    private CollatorGraph mergeDuplicateGraphs(List<CollatorGraph> duplicateGraphs) {
        CollatorGraph firstGraph = duplicateGraphs.get(0);
        return CollatorGraph.builder()
                .nodes(firstGraph.getNodes())
                .edges(mergeDuplicateEdges(duplicateGraphs))
                .sampleSize(duplicateGraphs.size())
                .build();
    }

    private List<CollatorGraphEdge> mergeDuplicateEdges(List<CollatorGraph> duplicateGraphs) {
        CollatorGraph firstCallGraph = duplicateGraphs.get(0);
        return IntStream.range(0, firstCallGraph.getEdges().size())
                .mapToObj((EdgeIndex) -> mergeDuplicateEdges(duplicateGraphs, EdgeIndex))
                .collect(toUnmodifiableList());
    }

    private CollatorGraphEdge mergeDuplicateEdges(List<CollatorGraph> duplicateGraphs, int edgeIndex) {
        List<CollatorGraphEdge> duplicateEdges = duplicateGraphs.stream()
                .map(CollatorGraph::getEdges)
                .map(edges -> edges.get(edgeIndex))
                .collect(toUnmodifiableList());

        return edgeHelper.mergeDuplicateEdges(duplicateEdges);
    }
}
