package tech.kronicle.tracingprocessor;

import lombok.Value;
import tech.kronicle.sdk.models.SummaryComponentDependency;
import tech.kronicle.sdk.models.SummaryComponentDependencyNode;
import tech.kronicle.sdk.models.SummaryDependencies;

import java.util.List;

@Value
public class MutableComponentDependencies implements SummaryDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> {

    List<SummaryComponentDependencyNode> nodes;
    List<SummaryComponentDependency> dependencies;
}
