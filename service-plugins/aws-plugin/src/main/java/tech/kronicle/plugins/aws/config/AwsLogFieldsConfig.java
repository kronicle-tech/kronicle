package tech.kronicle.plugins.aws.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;

@Value
public class AwsLogFieldsConfig {

    @NotEmpty
    String level;
    @NotEmpty
    String message;
}
