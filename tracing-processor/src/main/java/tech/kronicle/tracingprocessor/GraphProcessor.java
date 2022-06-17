package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.sdk.models.Diagram;
import tech.kronicle.sdk.models.GraphEdge;
import tech.kronicle.tracingprocessor.internal.constants.DiagramTypes;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraphEdge;
import tech.kronicle.tracingprocessor.internal.models.TimestampsForEdge;
import tech.kronicle.tracingprocessor.internal.services.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
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
        addDiagramIfNonNull(diagrams, toDiagram(
                tracingData,
                componentGraphCollator.collateGraph(tracingData),
                "",
                "",
                DiagramTypes.TRACING
        ));
        addDiagramIfNonNull(diagrams, toDiagram(
                tracingData,
                subComponentGraphCollator.collateGraph(tracingData),
                "-subcomponents",
                " - Subcomponents",
                DiagramTypes.TRACING
        ));
        diagrams.addAll(toDiagrams(
                tracingData,
                callGraphCollator.collateCallGraphs(tracingData),
                "-call-graph",
                " - Call Graph",
                DiagramTypes.CALL_GRAPH
        ));
        return diagrams;
    }

    private void addDiagramIfNonNull(List<Diagram> diagrams, Diagram newDiagram) {
        if (nonNull(newDiagram)) {
            diagrams.add(newDiagram);
        }
    }

    public Diagram processDiagram(Diagram diagram) {
        return diagramGraphCollator.collateGraph(diagram);
    }

    private Collection<Diagram> toDiagrams(TracingData tracingData, List<CollatorGraph> graphs, String idSuffix, String nameSuffix, String diagramType) {
        return IntStream.range(0, graphs.size())
                .mapToObj(graphIndex -> toDiagram(
                        tracingData,
                        graphs.get(graphIndex),
                        idSuffix + "-" + (graphIndex + 1),
                        nameSuffix + " " + (graphIndex + 1),
                        diagramType
                ))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Diagram toDiagram(TracingData tracingData, CollatorGraph graph, String idSuffix, String nameSuffix, String diagramType) {
        if (graph.getEdges().isEmpty()) {
            return null;
        }
        Diagram diagram = tracingData.toDiagram(
                diagramType,
                graph.getNodes(),
                toGraphEdges(graph.getEdges()),
                graph.getSampleSize()
        );
        return diagram.toBuilder()
                .id(diagram.getId() + idSuffix)
                .name(diagram.getName() + nameSuffix)
                .build();
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
