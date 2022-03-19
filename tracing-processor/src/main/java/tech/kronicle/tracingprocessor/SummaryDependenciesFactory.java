package tech.kronicle.tracingprocessor;

import tech.kronicle.sdk.models.ObjectWithComponentId;
import tech.kronicle.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import tech.kronicle.sdk.models.SummaryDependencies;

import java.util.List;

@FunctionalInterface
public interface SummaryDependenciesFactory<N extends ObjectWithComponentId, D extends ObjectWithSourceIndexAndTargetIndex, S extends SummaryDependencies<N, D>> {

    S create(List<N> nodes, List<D> dependencies);
}
