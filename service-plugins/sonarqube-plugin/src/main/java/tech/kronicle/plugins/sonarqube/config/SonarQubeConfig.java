package tech.kronicle.plugins.sonarqube.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import java.util.List;

@Value
public class SonarQubeConfig {

    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    @NotNull
    Duration timeout;
    List<String> organizations;
}
