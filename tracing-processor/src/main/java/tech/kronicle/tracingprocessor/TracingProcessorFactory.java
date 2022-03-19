package tech.kronicle.tracingprocessor;

public final class TracingProcessorFactory {

    public static TracingProcessor createTracingProcessor() {
        GenericDependencyCollator genericDependencyCollator = new GenericDependencyCollator();
        DependencyDurationCalculator dependencyDurationCalculator = new DependencyDurationCalculator();
        DependencyHelper dependencyHelper = new DependencyHelper(dependencyDurationCalculator);
        return new TracingProcessor(
                new ComponentDependencyCollator(genericDependencyCollator, dependencyHelper),
                new SubComponentDependencyCollator(genericDependencyCollator, dependencyHelper),
                new CallGraphCollator(genericDependencyCollator, dependencyHelper, dependencyDurationCalculator)
        );
    }

    private TracingProcessorFactory() {
    }
}
