package tech.kronicle.plugins.gradle.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GradleDependencies {

    List<Configuration> configurations;
}
