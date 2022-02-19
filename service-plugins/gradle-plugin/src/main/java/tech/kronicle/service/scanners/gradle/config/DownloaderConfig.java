package tech.kronicle.service.scanners.gradle.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
@AllArgsConstructor
public class DownloaderConfig {

    @NotNull
    Duration timeout;
}
