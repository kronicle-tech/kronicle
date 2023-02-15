package tech.kronicle.plugins.zipkin.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;

@Value
public class ZipkinConfig {

    @NotNull
    Boolean enabled;
    @NotEmpty
    @Pattern(regexp = "https?://.+[^/]")
    String baseUrl;
    @NotNull
    Duration timeout;
    String cookieName;
    String cookieValue;
    Integer traceLimit;
}
