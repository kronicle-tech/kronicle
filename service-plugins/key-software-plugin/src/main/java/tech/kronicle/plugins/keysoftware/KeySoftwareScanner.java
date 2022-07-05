package tech.kronicle.plugins.keysoftware;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionComparator;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.Version;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionParser;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.scanners.LateComponentScanner;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.keysoftware.config.KeySoftwareRuleConfig;
import tech.kronicle.plugins.keysoftware.services.KeySoftwareRuleProvider;
import tech.kronicle.sdk.models.*;

import javax.inject.Inject;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Extension
@Slf4j
public class KeySoftwareScanner extends LateComponentScanner {

    private static final Duration CACHE_TTL = Duration.ZERO;

    private final Map<String, Pattern> patternCache = new HashMap<>();
    private final VersionParser versionParser = new VersionParser();
    private final Comparator<Version> versionComparator = new DefaultVersionComparator().asVersionComparator().reversed();
    private final List<KeySoftwareRuleConfig> rules;

    @Inject
    public KeySoftwareScanner(KeySoftwareRuleProvider ruleProvider) {
        this.rules = ruleProvider.getRules();
    }

    @Override
    public String id() {
        return "key-software";
    }

    @Override
    public String description() {
        return "Processes all software found by other scanners and looks for certain configured `key software` to "
                + "find what version(s), if any, a component uses.  Key software is typically things like Gradle "
                + "and Spring Boot";
    }

    @Override
    public Output<Void, Component> scan(Component input) {
        if (rules.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        List<Software> softwares = getSoftwares(input);

        if (softwares.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        List<KeySoftware> keySoftware = getKeySoftware(softwares);

        if (keySoftware.isEmpty()) {
            return Output.empty(CACHE_TTL);
        }

        List<Tag> keySoftwareTags = createKeySoftwareTags(keySoftware);

        return Output.ofTransformer(
                component -> component.addState(new KeySoftwaresState(KeySoftwarePlugin.ID, keySoftware))
                        .addTags(keySoftwareTags),
                CACHE_TTL
        );
    }

    private List<Software> getSoftwares(Component input) {
        List<SoftwaresState> states = input.getStates(SoftwaresState.TYPE);
        return states.stream()
                .map(SoftwaresState::getSoftwares)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private List<KeySoftware> getKeySoftware(List<Software> softwares) {
        return rules.stream()
                .map(applyRule(softwares))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private Function<KeySoftwareRuleConfig, KeySoftware> applyRule(List<Software> softwares) {
        return (KeySoftwareRuleConfig rule) -> {
            List<String> versions = applyRule(softwares, rule);

            if (versions.isEmpty()) {
                return null;
            }

            return new KeySoftware(rule.getName(), versions);
        };
    }

    private List<String> applyRule(List<Software> softwares, KeySoftwareRuleConfig rule) {
        Pattern softwareNamePattern = getCachedPattern(rule.getSoftwareNamePattern());
        return softwares.stream()
                .filter(software -> softwareNamePattern.matcher(software.getName()).find())
                .map(Software::getVersion)
                .map(versionParser::transform)
                .sorted(versionComparator)
                .map(Version::getSource)
                .distinct()
                .collect(toUnmodifiableList());
    }

    private Pattern getCachedPattern(String pattern) {
        Pattern compiledPattern = patternCache.get(pattern);

        if (isNull(compiledPattern)) {
            compiledPattern = Pattern.compile(pattern);
            patternCache.put(pattern, compiledPattern);
        }

        return compiledPattern;
    }

    private List<Tag> createKeySoftwareTags(List<KeySoftware> keySoftware) {
        return keySoftware.stream()
                .map(this::mapKeySoftwareToTag)
                .collect(toUnmodifiableList());
    }

    private Tag mapKeySoftwareToTag(KeySoftware keySoftware) {
        return new Tag(
                keySoftware.getName(),
                String.join(", ", keySoftware.getVersions())
        );
    }
}
