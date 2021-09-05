package tech.kronicle.service.scanners.gradle.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@ConfigurationProperties("gradle")
@ConstructorBinding
@Value
@NonFinal
public class GradleConfig {

    List<String> additionalSafeSoftwareRepositoryUrls;
    @NotEmpty
    String pomCacheDir;
}
