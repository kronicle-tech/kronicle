package tech.kronicle.service.scanners.sonarqube.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@ConfigurationProperties("sonarqube")
@ConstructorBinding
@Value
@NonFinal
public class SonarQubeConfig {

    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    List<String> expectedComponentTypeIds;
}
