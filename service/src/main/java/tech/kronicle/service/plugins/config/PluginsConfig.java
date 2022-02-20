package tech.kronicle.service.plugins.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties("plugins")
@ConstructorBinding
@Value
@NonFinal
public class PluginsConfig {

    @NotNull
    String mode;
    @NotEmpty
    List<String> pluginRootDirs;

}
