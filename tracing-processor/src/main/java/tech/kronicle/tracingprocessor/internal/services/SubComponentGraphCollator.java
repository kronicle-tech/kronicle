package tech.kronicle.tracingprocessor.internal.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.pluginapi.finders.models.TracingData;
import tech.kronicle.tracingprocessor.internal.models.CollatorGraph;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SubComponentGraphCollator {

    private final GenericGraphCollator genericGraphCollator;
    private final EdgeHelper edgeHelper;

    public CollatorGraph collateGraph(TracingData tracingData) {
        return genericGraphCollator.createGraph(
                tracingData.getTraces(),
                edgeHelper::createSubComponentNode,
                NodeComparators.SUB_COMPONENT_NODE_COMPARATOR,
                edgeHelper::mergeDuplicateEdges
        );
    }

}
