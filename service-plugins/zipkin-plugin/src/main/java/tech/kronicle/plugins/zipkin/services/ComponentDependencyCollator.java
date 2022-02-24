package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.kronicle.sdk.models.Dependency;
import tech.kronicle.sdk.models.SummaryComponentDependencies;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.plugins.zipkin.models.NodesAndDependencies;
import tech.kronicle.plugins.zipkin.models.api.Span;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentDependencyCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final Comparator<SummaryComponentDependencyNode> componentNodeComparator;
    private final DependencyHelper dependencyHelper;

    public SummaryComponentDependencies collateDependencies(List<List<Span>> traces, List<Dependency> otherDependencies) {
        NodesAndDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> nodesAndDependencies = genericDependencyCollator.createDependencies(
                traces, this::createComponentDependencyNode, componentNodeComparator, dependencyHelper::mergeDuplicateDependencies);
        addOtherComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies(), otherDependencies);
        return createComponentDependencies(nodesAndDependencies);
    }

    private SummaryComponentDependencyNode createComponentDependencyNode(Span span) {
        return new SummaryComponentDependencyNode(span.getLocalEndpoint().getServiceName());
    }

    private SummaryComponentDependencies createComponentDependencies(
            NodesAndDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> nodesAndDependencies) {
        return new SummaryComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies());
    }

    private void addOtherComponentDependencies(List<SummaryComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies,
            List<Dependency> otherComponentDependencies) {
        Map<Boolean, List<Dependency>> grouped = otherComponentDependencies.stream()
                .distinct()
                .collect(Collectors.groupingBy(otherComponentDependency ->
                        dependencyAlreadyExists(nodes, dependencies, otherComponentDependency)));

        Optional.ofNullable(grouped.get(true)).ifPresent(duplicateDependencies -> duplicateDependencies.forEach(otherComponentDependency ->
                log.warn("Other component dependency \"{}\" is a duplicate of a dependency in Zipkin and should be removed",
                        otherComponentDependency.reference())));

        Optional.ofNullable(grouped.get(false)).ifPresent(newDependencies -> createDependenciesForOtherDependencies(nodes, newDependencies)
                .forEach(dependencies::add));
    }

    private Stream<SummaryComponentDependency> createDependenciesForOtherDependencies(List<SummaryComponentDependencyNode> nodes,
            List<Dependency> newDependencies) {
        return newDependencies.stream().map(otherComponentDependency -> createDependencyForOtherDependency(nodes, otherComponentDependency));
    }

    private SummaryComponentDependency createDependencyForOtherDependency(List<SummaryComponentDependencyNode> nodes,
            Dependency otherComponentDependency) {
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
