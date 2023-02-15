package tech.kronicle.service.plugins.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Validated
@ConfigurationProperties("plugin-manager")
@Value
public class PluginManagerConfig {

    @NotNull
    String mode;
    @NotEmpty
    List<String> pluginRootDirs;
}
