package tech.kronicle.service.scanners.gradle.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class PomCacheConfig {

    @NotEmpty
    String dir;
}
