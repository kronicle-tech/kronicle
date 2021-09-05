package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SummaryComponentDependencies {

    @NotNull
    List<@NotNull @Valid SummaryComponentDependencyNode> nodes;
    @NotNull
    List<@NotNull @Valid SummaryComponentDependency> dependencies;

    public SummaryComponentDependencies(List<SummaryComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies) {
        this.nodes = ListUtils.createUnmodifiableList(nodes);
        this.dependencies = ListUtils.createUnmodifiableList(dependencies);
    }
}
