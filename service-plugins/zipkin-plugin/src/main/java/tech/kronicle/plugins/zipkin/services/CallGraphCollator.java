package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import tech.kronicle.plugins.zipkin.models.CollatorComponentDependency;
import tech.kronicle.plugins.zipkin.models.NodesAndDependencies;
import tech.kronicle.plugins.zipkin.models.ObjectWithDurations;
import tech.kronicle.plugins.zipkin.models.ObjectWithTimestamps;
import tech.kronicle.plugins.zipkin.models.SourceIndexAndTargetIndex;
import tech.kronicle.plugins.zipkin.models.TimestampsForDependency;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import tech.kronicle.sdk.models.SummaryCallGraph;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CallGraphCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final Comparator<SummarySubComponentDependencyNode> subComponentNodeComparator;
    private final DependencyHelper dependencyHelper;
    private final DependencyDurationCalculator dependencyDurationCalculator;

    public List<SummaryCallGraph> collateCallGraphs(List<List<Span>> traces) {
        return traces.stream()
                .map(trace -> genericDependencyCollator.createDependencies(List.of(trace), dependencyHelper::createSubComponentDependencyNode,
                        subComponentNodeComparator, this::mergeDuplicateDependencies))
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

    private CallGraph createCallGraphs(
            NodesAndDependencies<SummarySubComponentDependencyNode, CallGraphDependency> nodesAndDependencies) {
        return new CallGraph(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies(), 1);
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
