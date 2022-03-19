package tech.kronicle.sdk.models;

import java.util.List;

public interface SummaryDependencies<N, D> {

    List<N> getNodes();
    List<D> getDependencies();
}
