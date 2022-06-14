package tech.kronicle.tracingprocessor.internal.services;

import tech.kronicle.sdk.models.GraphNode;
import tech.kronicle.utils.TagListComparator;

import java.util.Comparator;

public final class NodeComparators {

    public static final Comparator<GraphNode> COMPONENT_NODE_COMPARATOR =  Comparator.comparing(GraphNode::getComponentId);

    public static final Comparator<GraphNode> SUB_COMPONENT_NODE_COMPARATOR = Comparator.comparing(GraphNode::getComponentId)
                .thenComparing(GraphNode::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(GraphNode::getTags, new TagListComparator());

    private NodeComparators() {
    }
}
