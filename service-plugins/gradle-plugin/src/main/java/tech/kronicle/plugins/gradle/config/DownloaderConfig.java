package tech.kronicle.plugins.gradle.config;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Value
public class DownloaderConfig {

    @NotNull
    Duration timeout;
}
