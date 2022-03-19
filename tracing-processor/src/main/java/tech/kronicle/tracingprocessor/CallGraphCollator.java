package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryDependencies;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CallGraphCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final DependencyHelper dependencyHelper;
    private final DependencyDurationCalculator dependencyDurationCalculator;

    public List<SummaryCallGraph> collateCallGraphs(List<GenericTrace> traces) {
        return traces.stream()
                .map(trace -> genericDependencyCollator.createDependencies(
                        List.of(trace),
                        dependencyHelper::createSubComponentDependencyNode,
                        NodeComparators.SUB_COMPONENT_NODE_COMPARATOR,
                        this::mergeDuplicateDependencies,
                        CallGraphDependencies::new
                ))
                .map(this::createCallGraphs)
                .collect(Collectors.groupingBy(this::createSimpleCallGraph))
                .values()
                .stream()
                .map(this::mergeDuplicateCallGraphs)
                .collect(Collectors.toList());
    }

    private CallGraphDependency mergeDuplicateDependencies(List<CollatorComponentDependency> duplicateDependencies) {
        CollatorComponentDependency firstDependency = duplicateDependencies.get(0);
        return new CallGraphDependency(firstDependency.getSourceIndex(), firstDependency.getTargetIndex(),
                dependencyHelper.mergeRelatedIndexes(duplicateDependencies, CollatorComponentDependency::getRelatedIndexes),
                duplicateDependencies.size(),
                dependencyHelper.getFlattenedListValuesFromObjects(duplicateDependencies, CollatorComponentDependency::getTimestamps),
                dependencyHelper.getFlattenedListValuesFromObjects(duplicateDependencies, CollatorComponentDependency::getDurations));
    }

    private SummaryCallGraph mergeDuplicateCallGraphs(List<CallGraph> duplicateCallGraphs) {
        CallGraph firstCallGraph = duplicateCallGraphs.get(0);
        return new SummaryCallGraph(firstCallGraph.getNodes(), mergeDuplicateCallGraphDependencies(duplicateCallGraphs),
                duplicateCallGraphs.size());
    }

    private List<SummaryComponentDependency> mergeDuplicateCallGraphDependencies(List<CallGraph> duplicateCallGraphs) {
        CallGraph firstCallGraph = duplicateCallGraphs.get(0);
        return IntStream.range(0, firstCallGraph.dependencies.size())
                .mapToObj((dependencyIndex) -> mergeDuplicateCallGraphDependencies(duplicateCallGraphs, dependencyIndex))
                .collect(Collectors.toList());
    }

    private SummaryComponentDependency mergeDuplicateCallGraphDependencies(List<CallGraph> duplicateCallGraphs, int dependencyIndex) {
        List<CallGraphDependency> duplicateDependencies = duplicateCallGraphs.stream()
                .map(CallGraph::getDependencies)
                .map(dependencies -> dependencies.get(dependencyIndex))
                .collect(Collectors.toList());
        CallGraphDependency firstDependency = duplicateDependencies.get(0);
        TimestampsForDependency timestampsForDependency = dependencyHelper.getTimestampsForDependency(duplicateDependencies);
        return new SummaryComponentDependency(firstDependency.sourceIndex, firstDependency.targetIndex,
                dependencyHelper.mergeRelatedIndexes(duplicateDependencies, CallGraphDependency::getRelatedIndexes), false,
                getSampleSize(duplicateDependencies), timestampsForDependency.getStartTimestamp(), timestampsForDependency.getEndTimestamp(),
                dependencyDurationCalculator.calculateDependencyDuration(duplicateDependencies));
    }

    private Integer getSampleSize(List<CallGraphDependency> duplicateDependencies) {
        return duplicateDependencies.stream()
                .mapToInt(CallGraphDependency::getSampleSize)
                .sum();
    }

    private CallGraph createCallGraphs(CallGraphDependencies callGraphDependencies) {
        return new CallGraph(callGraphDependencies.getNodes(), callGraphDependencies.getDependencies(), 1);
    }

    private SimpleCallGraph createSimpleCallGraph(CallGraph callGraph) {
        return new SimpleCallGraph(callGraph.getNodes(), callGraph.getDependencies().stream().map(
                SourceIndexAndTargetIndex::new).collect(Collectors.toList()));
    }

    @Value
    private static class SimpleCallGraph {

        List<SummarySubComponentDependencyNode> nodes;
        List<SourceIndexAndTargetIndex> dependencies;
    }

    @Value
    public static class CallGraphDependencies implements SummaryDependencies<SummarySubComponentDependencyNode, CallGraphDependency> {

        List<SummarySubComponentDependencyNode> nodes;
        List<CallGraphDependency> dependencies;
    }

    @Value
    public static class CallGraphDependency implements ObjectWithSourceIndexAndTargetIndex, ObjectWithTimestamps, ObjectWithDurations {

        Integer sourceIndex;
        Integer targetIndex;
        List<Integer> relatedIndexes;
        Integer sampleSize;
        List<Long> timestamps;
        List<Long> durations;
    }

    @Value
    private static class CallGraph {
        List<SummarySubComponentDependencyNode> nodes;
        List<CallGraphDependency> dependencies;
        Integer traceCount;
    }
}
