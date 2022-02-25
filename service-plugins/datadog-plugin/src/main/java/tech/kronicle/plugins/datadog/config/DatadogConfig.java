package tech.kronicle.plugins.datadog.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;

@Validated
@Value
@NonFinal
public class DatadogConfig {

    String baseUrl;
    String apiKey;
    String applicationKey;
    DatadogDependenciesConfig dependencies;
}
