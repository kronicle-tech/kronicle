package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SummaryComponentDependencies implements SummaryDependencies<SummaryComponentDependencyNode, SummaryComponentDependency> {

    @NotNull
    List<@NotNull @Valid SummaryComponentDependencyNode> nodes;
    @NotNull
    List<@NotNull @Valid SummaryComponentDependency> dependencies;

    public SummaryComponentDependencies(List<SummaryComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies) {
        this.nodes = createUnmodifiableList(nodes);
        this.dependencies = createUnmodifiableList(dependencies);
    }
}
