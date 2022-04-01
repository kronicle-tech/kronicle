package tech.kronicle.sdk.models;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.utils.ListUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@NonFinal
@SuperBuilder(toBuilder = true)
@Jacksonized
public class SummarySubComponentDependencies implements SummaryDependencies<SummarySubComponentDependencyNode, SummaryComponentDependency> {

    @NotNull
    List<@NotNull @Valid SummarySubComponentDependencyNode> nodes;
    @NotNull
    List<@NotNull @Valid SummaryComponentDependency> dependencies;

    public SummarySubComponentDependencies(List<SummarySubComponentDependencyNode> nodes, List<SummaryComponentDependency> dependencies) {
        this.nodes = createUnmodifiableList(nodes);
        this.dependencies = createUnmodifiableList(dependencies);
    }
}
