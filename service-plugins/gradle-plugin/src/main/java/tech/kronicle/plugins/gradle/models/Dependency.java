package tech.kronicle.plugins.gradle.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Dependency {

    String name;
    String reason;
}
