package tech.kronicle.service.scanners.keysoftware.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties("key-software")
@ConstructorBinding
@Value
@NonFinal
public class KeySoftwareConfig {

    @NotEmpty
    List<@NotNull KeySoftwareRule> rules;
}
