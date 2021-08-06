package com.moneysupermarket.componentcatalog.service.scanners.zipkin.services;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.ComponentDependency;
import com.moneysupermarket.componentcatalog.sdk.models.DependencyDirection;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencies;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependency;
import com.moneysupermarket.componentcatalog.sdk.models.SummaryComponentDependencyNode;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.NodesAndDependencies;
import com.moneysupermarket.componentcatalog.service.scanners.zipkin.models.api.Span;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public SummaryComponentDependencies collateDependencies(List<List<Span>> traces, List<Component> components) {
        NodesAndDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> nodesAndDependencies = genericDependencyCollator.createDependencies(
                traces, this::createComponentDependencyNode, componentNodeComparator, dependencyHelper::mergeDuplicateDependencies);
        addManualComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies(),
                getManualComponentDependencies(components));
        return createComponentDependencies(nodesAndDependencies);
    }

    private SummaryComponentDependencyNode createComponentDependencyNode(Span span) {
        return new SummaryComponentDependencyNode(span.getLocalEndpoint().getServiceName());
    }

    private List<CollatorManualComponentDependency> getManualComponentDependencies(List<Component> components) {
        return components.stream()
                .flatMap(component -> component.getDependencies().stream()
                        .map(dependency -> createManualComponentDependency(component, dependency)))
                .distinct()
                .collect(Collectors.toList());
    }

    private CollatorManualComponentDependency createManualComponentDependency(Component component, ComponentDependency dependency) {
        if (Objects.equals(dependency.getDirection(), DependencyDirection.INBOUND)) {
            return new CollatorManualComponentDependency(dependency.getTargetComponentId(), component.getId());
        } else {
            return new CollatorManualComponentDependency(component.getId(), dependency.getTargetComponentId());
        }
    }

    private SummaryComponentDependencies createComponentDependencies(
            NodesAndDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> nodesAndDependencies) {
        return new SummaryComponentDependencies(nodesAndDependencies.getNodes(), nodesAndDependencies.getDependencies());
    }

    private void addManualComponentDependencies(List<SummaryComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies,
            List<CollatorManualComponentDependency> manualComponentDependencies) {
        Map<Boolean, List<CollatorManualComponentDependency>> grouped = manualComponentDependencies.stream()
                .collect(Collectors.groupingBy(manualComponentDependency ->
                        dependencyAlreadyExists(nodes, dependencies, manualComponentDependency)));

        Optional.ofNullable(grouped.get(true)).ifPresent(duplicateDependencies -> duplicateDependencies.forEach(manualComponentDependency ->
                log.warn("Manual component dependency \"{}\" is a duplicate of a dependency in Zipkin and should be removed",
                        manualComponentDependency.reference())));

        Optional.ofNullable(grouped.get(false)).ifPresent(newDependencies -> createDependenciesForManualDependencies(nodes, newDependencies)
                .forEach(dependencies::add));
    }

    private Stream<SummaryComponentDependency> createDependenciesForManualDependencies(List<SummaryComponentDependencyNode> nodes,
            List<CollatorManualComponentDependency> newDependencies) {
        return newDependencies.stream().map(manualComponentDependency -> createDependencyForManualDependency(nodes, manualComponentDependency));
    }

    private SummaryComponentDependency createDependencyForManualDependency(List<SummaryComponentDependencyNode> nodes,
            CollatorManualComponentDependency manualComponentDependency) {
        return new SummaryComponentDependency(getOrAddNode(nodes, manualComponentDependency.sourceComponentId),
                getOrAddNode(nodes, manualComponentDependency.targetComponentId), List.of(), true, 0, null, null, null);
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
            CollatorManualComponentDependency manualComponentDependency) {
        return dependencies.stream()
                .anyMatch(dependency -> nodeComponentIdEqualsComponentId(nodes, dependency.getSourceIndex(), manualComponentDependency.sourceComponentId)
                        && nodeComponentIdEqualsComponentId(nodes, dependency.getTargetIndex(), manualComponentDependency.targetComponentId));
    }

    private boolean nodeComponentIdEqualsComponentId(List<SummaryComponentDependencyNode> nodes, Integer index, String componentId) {
        return nonNull(index) && Objects.equals(getNodeComponentIdByIndex(nodes, index), componentId);
    }

    private String getNodeComponentIdByIndex(List<SummaryComponentDependencyNode> nodes, int index) {
        return nodes.get(index).getComponentId();
    }

    @Value
    private static class CollatorManualComponentDependency implements ObjectWithReference {

        String sourceComponentId;
        String targetComponentId;

        public String reference() {
            return sourceComponentId + " to " + targetComponentId;
        }
    }
}
