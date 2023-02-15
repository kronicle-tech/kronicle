package tech.kronicle.plugins.keysoftware.config;

import lombok.Value;

import jakarta.validation.constraints.NotBlank;

@Value
public class KeySoftwareRuleConfig {

    @NotBlank
    String softwareNamePattern;
    @NotBlank
    String name;
}
