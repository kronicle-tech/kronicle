package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.GraphEdge;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;
import tech.kronicle.tracingprocessor.internal.models.TimestampsForEdge;
import tech.kronicle.tracingprocessor.internal.services.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class GraphProcessor {

    private final DiagramGraphCollator diagramGraphCollator;
    private final ComponentGraphCollator componentGraphCollator;
    private final SubComponentGraphCollator subComponentGraphCollator;
    private final CallGraphCollator callGraphCollator;
    private final EdgeHelper edgeHelper;
    private final EdgeDurationCalculator edgeDurationCalculator;

    public List<Diagram> processTracingData(TracingData tracingData) {
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(toDiagram(tracingData, componentGraphCollator.collateGraph(tracingData)));
        diagrams.add(toDiagram(tracingData, subComponentGraphCollator.collateGraph(tracingData)));
        diagrams.addAll(toDiagrams(tracingData, callGraphCollator.collateCallGraphs(tracingData)));
        return diagrams;
    }

    public Diagram processDiagram(Diagram diagram) {
        return diagramGraphCollator.collateGraph(diagram);
    }

    private Collection<Diagram> toDiagrams(TracingData tracingData, List<CollatorGraph> graphs) {
        return graphs.stream()
                .map(graph -> toDiagram(tracingData, graph))
                .collect(toUnmodifiableList());
    }

    private Diagram toDiagram(TracingData tracingData, CollatorGraph graph) {
        return tracingData.toDiagram(graph.getNodes(), toGraphEdges(graph.getEdges()), graph.getSampleSize());
    }

    private List<GraphEdge> toGraphEdges(List<CollatorGraphEdge> edges) {
        return edges.stream()
                .map(this::toGraphEdge)
                .collect(toUnmodifiableList());
    }

    private GraphEdge toGraphEdge(CollatorGraphEdge edge) {
        TimestampsForEdge timestampsForEdge = edgeHelper.getTimestampsForEdge(edge.getTimestamps());
        return new GraphEdge(
                edge.getSourceIndex(),
                edge.getTargetIndex(),
                edge.getRelatedIndexes(),
                edge.getType(),
                edge.getLabel(),
                edge.getDescription(),
                edge.getSampleSize(),
                timestampsForEdge.getStartTimestamp(),
                timestampsForEdge.getEndTimestamp(),
                edgeDurationCalculator.calculateEdgeDuration(edge.getDurations())
        );
    }

}
