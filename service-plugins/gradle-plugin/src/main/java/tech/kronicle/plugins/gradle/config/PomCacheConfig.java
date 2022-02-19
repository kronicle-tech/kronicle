package tech.kronicle.plugins.gradle.config;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class PomCacheConfig {

    @NotEmpty
    String dir;
}
