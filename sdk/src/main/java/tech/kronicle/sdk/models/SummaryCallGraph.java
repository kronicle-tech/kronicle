package tech.kronicle.sdk.models;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@Jacksonized
public class SummaryCallGraph extends SummarySubComponentDependencies {

    Integer traceCount;

    public SummaryCallGraph(List<SummarySubComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies, Integer traceCount) {
        super(nodes, dependencies);
        this.traceCount = traceCount;
    }
}
