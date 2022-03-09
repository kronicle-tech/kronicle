package tech.kronicle.plugins.aws.xray.models;

import lombok.Value;

import java.util.List;

@Value
public class Service {

    String name;
    List<String> names;
    List<Edge> edges;
}
