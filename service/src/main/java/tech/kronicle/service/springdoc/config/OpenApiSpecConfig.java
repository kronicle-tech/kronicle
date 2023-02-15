package tech.kronicle.service.springdoc.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("openapi-spec")
@Value
public class OpenApiSpecConfig {

    Boolean clearExistingServers;
    List<OpenApiSpecServerConfig> servers;
}
