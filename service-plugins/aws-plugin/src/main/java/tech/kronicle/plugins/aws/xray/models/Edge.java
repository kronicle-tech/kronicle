package tech.kronicle.plugins.aws.xray.models;

import lombok.Value;

import java.util.List;

@Value
public class Edge {

    List<Alias> aliases;
}
