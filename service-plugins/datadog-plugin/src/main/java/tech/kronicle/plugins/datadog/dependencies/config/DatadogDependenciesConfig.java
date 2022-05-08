package tech.kronicle.plugins.datadog.dependencies.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

@Value
public class DatadogDependenciesConfig {

    List<String> environments;
}
