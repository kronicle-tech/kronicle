package tech.kronicle.plugins.zipkin.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.Duration;

@Value
@NonFinal
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
