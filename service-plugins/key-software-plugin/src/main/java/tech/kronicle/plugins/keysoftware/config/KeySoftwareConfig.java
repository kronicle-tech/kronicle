package tech.kronicle.plugins.keysoftware.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value
public class KeySoftwareConfig {

    @NotNull
    Boolean defaultRulesEnabled;
    @NotEmpty
    List<@NotNull KeySoftwareRuleConfig> defaultRules;
    List<@NotNull KeySoftwareRuleConfig> rules;
}
