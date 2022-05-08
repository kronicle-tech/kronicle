package tech.kronicle.service.plugins.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties("plugin-manager")
@ConstructorBinding
@Value
public class PluginManagerConfig {

    @NotNull
    String mode;
    @NotEmpty
    List<String> pluginRootDirs;
}
