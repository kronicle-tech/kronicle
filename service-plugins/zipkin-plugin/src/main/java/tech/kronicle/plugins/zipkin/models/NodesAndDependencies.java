package tech.kronicle.plugins.zipkin.models;

import lombok.Value;

import java.util.List;

@Value
public class NodesAndDependencies<N, D> {

    List<N> nodes;
    List<D> dependencies;
}