package tech.kronicle.tracingprocessor;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.GenericTrace;
import tech.kronicle.sdk.models.SummarySubComponentDependencies;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SubComponentDependencyCollator {

    private final GenericDependencyCollator genericDependencyCollator;
    private final DependencyHelper dependencyHelper;

    public SummarySubComponentDependencies collateDependencies(List<GenericTrace> traces) {
        return genericDependencyCollator.createDependencies(
                traces,
                dependencyHelper::createSubComponentDependencyNode,
                NodeComparators.SUB_COMPONENT_NODE_COMPARATOR,
                dependencyHelper::mergeDuplicateDependencies,
                SummarySubComponentDependencies::new
        );
    }

}
