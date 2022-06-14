package tech.kronicle.tracingprocessor;

import tech.kronicle.tracingprocessor.internal.services.*;

public final class TracingProcessorFactory {

    public static TracingProcessor createTracingProcessor() {
        GenericGraphCollator genericGraphCollator = new GenericGraphCollator();
        EdgeHelper edgeHelper = new EdgeHelper();
        return new TracingProcessor(
                new ComponentGraphCollator(genericGraphCollator, edgeHelper),
                new SubComponentGraphCollator(genericGraphCollator, edgeHelper),
                new CallGraphCollator(genericGraphCollator, edgeHelper),
                edgeHelper,
                new EdgeDurationCalculator()
        );
    }

    private TracingProcessorFactory() {
    }
}
