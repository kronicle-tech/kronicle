package tech.kronicle.plugins.zipkin.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.zipkin.models.api.Span;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SubComponentDependencyCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final DependencyHelper dependencyHelper;

    public SummarySubComponentDependencies collateDependencies(List<List<Span>> traces) {
        return dependencyHelper.createSubComponentDependencies(genericDependencyCollator.createDependencies(
                traces,
                dependencyHelper::createSubComponentDependencyNode,
                NodeComparators.SUB_COMPONENT_NODE_COMPARATOR,
                dependencyHelper::mergeDuplicateDependencies
        ));
    }
}
