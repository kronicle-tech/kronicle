package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
@NonFinal
public class AwsLogFieldsConfig {

    @NotEmpty
    String level;
}
