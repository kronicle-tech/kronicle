package tech.kronicle.tracingprocessor;

import tech.kronicle.tracingprocessor.internal.services.*;

public final class GraphProcessorFactory {

    public static GraphProcessor createTracingProcessor() {
        GenericGraphCollator genericGraphCollator = new GenericGraphCollator();
        NodeHelper nodeHelper = new NodeHelper();
        EdgeHelper edgeHelper = new EdgeHelper();
        return new GraphProcessor(
                new DiagramGraphCollator(nodeHelper),
                new ComponentGraphCollator(genericGraphCollator, nodeHelper, edgeHelper),
                new SubComponentGraphCollator(genericGraphCollator, edgeHelper),
                new CallGraphCollator(genericGraphCollator, edgeHelper),
                edgeHelper,
                new EdgeDurationCalculator()
        );
    }

    private GraphProcessorFactory() {
    }
}
