package tech.kronicle.tracingprocessor;

import tech.kronicle.sdk.models.ObjectWithComponentId;
import tech.kronicle.sdk.models.DependencyWithIdentity;
import tech.kronicle.sdk.models.SummaryDependencies;

import java.util.List;

@FunctionalInterface
public interface SummaryDependenciesFactory<N extends ObjectWithComponentId, D extends DependencyWithIdentity, S extends SummaryDependencies<N, D>> {

    S create(List<N> nodes, List<D> dependencies);
}
