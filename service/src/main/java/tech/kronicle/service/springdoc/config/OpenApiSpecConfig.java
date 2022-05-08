package tech.kronicle.service.springdoc.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("openapi-spec")
@ConstructorBinding
@Value
public class OpenApiSpecConfig {

    Boolean clearExistingServers;
    List<OpenApiSpecServerConfig> servers;
}
