package tech.kronicle.plugins.sonarqube.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.Duration;
import java.util.List;

@Value
@NonFinal
public class SonarQubeConfig {

    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    @NotNull
    Duration timeout;
    List<String> organizations;
}
