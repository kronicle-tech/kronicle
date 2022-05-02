package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
@NonFinal
public class AwsTagKeysConfig {

    @NotEmpty
    String component;
    @NotEmpty
    String environment;
    @NotEmpty
    String team;
}
