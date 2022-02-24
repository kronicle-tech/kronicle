package tech.kronicle.plugins.datadog.dependencies.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@ConstructorBinding
@Value
@NonFinal
public class DatadogDependenciesConfig {

    @NotNull
    Duration timeout;
    List<String> environments;
}
