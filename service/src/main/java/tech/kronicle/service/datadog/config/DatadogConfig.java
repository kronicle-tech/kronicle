package tech.kronicle.service.datadog.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("datadog")
@ConstructorBinding
@Value
@NonFinal
public class DatadogConfig {

    String baseUrl;
    String apiKey;
    String applicationKey;
}
