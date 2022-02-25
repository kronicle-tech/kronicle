package tech.kronicle.plugins.keysoftware.config;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Value
@NonFinal
public class KeySoftwareConfig {

    @NotNull
    Boolean defaultRulesEnabled;
    @NotEmpty
    List<@NotNull KeySoftwareRuleConfig> defaultRules;
    List<@NotNull KeySoftwareRuleConfig> rules;
}
