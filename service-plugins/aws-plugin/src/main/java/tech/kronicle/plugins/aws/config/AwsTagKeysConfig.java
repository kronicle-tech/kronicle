package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
public class AwsTagKeysConfig {

    // When adding any new tag keys, remember to also add then to ResourceMapper.resourceHasSupportedMetadata()
    @NotEmpty
    String aliases;
    @NotEmpty
    String component;
    @NotEmpty
    String description;
    @NotEmpty
    String environment;
    @NotEmpty
    String team;
}
