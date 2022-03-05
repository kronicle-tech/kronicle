package tech.kronicle.plugins.zipkin.services;

import tech.kronicle.utils.MapComparator;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummarySubComponentDependencyNode;

import java.util.Comparator;

public final class NodeComparators {

    public static final Comparator<SummaryComponentDependencyNode> COMPONENT_NODE_COMPARATOR =  Comparator.comparing(SummaryComponentDependencyNode::getComponentId);

    public static final Comparator<SummarySubComponentDependencyNode> SUB_COMPONENT_NODE_COMPARATOR = Comparator.comparing(SummarySubComponentDependencyNode::getComponentId)
                .thenComparing(SummarySubComponentDependencyNode::getSpanName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(SummarySubComponentDependencyNode::getTags, new MapComparator<>());

    private NodeComparators() {
    }
}
