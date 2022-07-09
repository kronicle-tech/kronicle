package tech.kronicle.plugins.kubernetes.config;

import lombok.Value;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
public class ClusterConfig {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String environmentId;
    @NotBlank
    String kubeConfig;
}
