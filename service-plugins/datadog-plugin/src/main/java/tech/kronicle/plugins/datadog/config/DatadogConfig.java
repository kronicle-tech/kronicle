package tech.kronicle.plugins.datadog.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Value
public class DatadogConfig {

    String baseUrl;
    @NotNull
    Duration timeout;
    String apiKey;
    String applicationKey;
    DatadogDependenciesConfig dependencies;
}
