package tech.kronicle.plugins.kubernetes.config;

import lombok.Value;
import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Value
public class ClusterConfig {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String environmentId;
    @NotBlank
    String kubeConfig;
    Boolean apiResourcesWithSupportedMetadataOnly;
    Boolean createContainerStatusChecks;
}
