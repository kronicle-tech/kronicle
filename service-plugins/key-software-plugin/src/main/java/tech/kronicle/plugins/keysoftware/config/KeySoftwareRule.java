package tech.kronicle.plugins.keysoftware.config;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class KeySoftwareRule {

    @NotBlank
    String softwareNamePattern;
    @NotBlank
    String name;
}
