package tech.kronicle.plugins.datadog.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;
import tech.kronicle.plugins.datadog.dependencies.config.DatadogDependenciesConfig;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class DatadogConfig {

    String baseUrl;
    String apiKey;
    String applicationKey;
    DatadogDependenciesConfig datadogDependencies;
}
