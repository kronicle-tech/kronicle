package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotEmpty;

@Value
public class AwsLogFieldsConfig {

    @NotEmpty
    String level;
    @NotEmpty
    String message;
}
