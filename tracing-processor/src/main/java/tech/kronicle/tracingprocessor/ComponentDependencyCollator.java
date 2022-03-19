package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.pluginapi.finders.models.GenericSpan;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class ComponentDependencyCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final DependencyHelper dependencyHelper;
    
    public SummaryComponentDependencies collateDependencies(List<GenericTrace> traces, List<Dependency> dependencies) {
        MutableComponentDependencies mutableDependencies = genericDependencyCollator.createDependencies(
                traces,
                this::createComponentDependencyNode,
                NodeComparators.COMPONENT_NODE_COMPARATOR,
                dependencyHelper::mergeDuplicateDependencies,
                MutableComponentDependencies::new
        );
        addOtherComponentDependencies(mutableDependencies.getNodes(), mutableDependencies.getDependencies(), dependencies);
        return createComponentDependencies(mutableDependencies);
    }

    private SummaryComponentDependencyNode createComponentDependencyNode(GenericSpan span) {
        return new SummaryComponentDependencyNode(span.getSourceName());
    }

    private SummaryComponentDependencies createComponentDependencies(
            MutableComponentDependencies nodesAndDependencies) {
        return new SummaryComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies());
    }

    private void addOtherComponentDependencies(
            List<SummaryComponentDependencyNode> nodes,
            List<SummaryComponentDependency> dependencies,
            List<Dependency> otherComponentDependencies
    ) {
        Map<Boolean, List<Dependency>> grouped = otherComponentDependencies.stream()
                .distinct()
                .collect(Collectors.groupingBy(
                        otherComponentDependency -> dependencyAlreadyExists(nodes, dependencies, otherComponentDependency)
                ));

        Optional.ofNullable(grouped.get(true)).ifPresent(duplicateDependencies -> duplicateDependencies.forEach(otherComponentDependency ->
                log.warn("Other component dependency \"{}\" is a duplicate of a dependency in Zipkin and should be removed",
                        otherComponentDependency.reference())));

        Optional.ofNullable(grouped.get(false)).ifPresent(newDependencies -> createDependenciesForOtherDependencies(nodes, newDependencies)
                .forEach(dependencies::add));
    }

    private Stream<SummaryComponentDependency> createDependenciesForOtherDependencies(
            List<SummaryComponentDependencyNode> nodes,
            List<Dependency> newDependencies
    ) {
        return newDependencies.stream().map(otherComponentDependency -> createDependencyForOtherDependency(nodes, otherComponentDependency));
    }

    private SummaryComponentDependency createDependencyForOtherDependency(
            List<SummaryComponentDependencyNode> nodes,
            Dependency otherComponentDependency
    ) {
        return new SummaryComponentDependency(getOrAddNode(nodes, otherComponentDependency.getSourceComponentId()),
                getOrAddNode(nodes, otherComponentDependency.getTargetComponentId()), List.of(), true, 0, null, null, null);
    }

    private int getOrAddNode(List<SummaryComponentDependencyNode> nodes, String componentId) {
        OptionalInt nodeIndexMatch = IntStream.range(0, nodes.size())
                .filter(nodeIndex -> Objects.equals(nodes.get(nodeIndex).getComponentId(), componentId))
                .findFirst();

        if (nodeIndexMatch.isPresent()) {
            return nodeIndexMatch.getAsInt();
        }

        nodes.add(new SummaryComponentDependencyNode(componentId));
        return nodes.size() - 1;
    }

    private boolean dependencyAlreadyExists(List<SummaryComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies,
                                            Dependency otherComponentDependency) {
        return dependencies.stream()
                .anyMatch(dependency -> nodeComponentIdEqualsComponentId(nodes, dependency.getSourceIndex(), otherComponentDependency.getSourceComponentId())
                        && nodeComponentIdEqualsComponentId(nodes, dependency.getTargetIndex(), otherComponentDependency.getTargetComponentId()));
    }

    private boolean nodeComponentIdEqualsComponentId(List<SummaryComponentDependencyNode> nodes, Integer index, String componentId) {
        return nonNull(index) && Objects.equals(getNodeComponentIdByIndex(nodes, index), componentId);
    }

    private String getNodeComponentIdByIndex(List<SummaryComponentDependencyNode> nodes, int index) {
        return nodes.get(index).getComponentId();
    }
}
