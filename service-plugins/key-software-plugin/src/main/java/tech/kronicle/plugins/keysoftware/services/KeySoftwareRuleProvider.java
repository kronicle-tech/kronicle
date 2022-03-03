package tech.kronicle.plugins.keysoftware.services;

import tech.kronicle.plugins.keysoftware.config.KeySoftwareConfig;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRuleConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class KeySoftwareRuleProvider {

    private final List<KeySoftwareRuleConfig> rules;

    @Inject
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
