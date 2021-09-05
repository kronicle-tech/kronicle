package tech.kronicle.service.scanners.zipkin.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties("zipkin")
@ConstructorBinding
@Value
@NonFinal
public class ZipkinConfig {

    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    @NotNull
    Duration timeout;
    String cookieName;
    String cookieValue;
    Integer traceLimit;
    List<String> expectedComponentTypeIds;
}
