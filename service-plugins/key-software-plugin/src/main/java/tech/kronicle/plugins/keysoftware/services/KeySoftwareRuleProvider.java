package tech.kronicle.plugins.keysoftware.services;

import org.springframework.stereotype.Service;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRuleConfig;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
public class KeySoftwareRuleProvider {

    private final List<KeySoftwareRuleConfig> rules;

    public KeySoftwareRuleProvider(KeySoftwareConfig config) {
        this.rules = Stream.concat(
                config.getDefaultRulesEnabled() ? config.getDefaultRules().stream() : Stream.empty(),
                nonNull(config.getRules()) ? config.getRules().stream() : Stream.empty()
        ).collect(Collectors.toUnmodifiableList());
    }

    public List<KeySoftwareRuleConfig> getRules() {
        return rules;
    }
}
