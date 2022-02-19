package tech.kronicle.plugins.sonarqube.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@Value
@NonFinal
public class SonarQubeConfig {

    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    List<String> organizations;
}
