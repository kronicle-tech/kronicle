package tech.kronicle.plugins.datadog.dependencies.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

@Validated
@Value
@NonFinal
public class DatadogDependenciesConfig {

    @NotNull
    Duration timeout;
    List<String> environments;
}
