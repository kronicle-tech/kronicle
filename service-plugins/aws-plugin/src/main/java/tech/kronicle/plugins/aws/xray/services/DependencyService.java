package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyService {

    private final XRayServiceGraphFetcher fetcher;
    private final DependencyAssembler assembler;
    private final AwsConfig config;

    public List<Dependency> getDependencies() {
        return assembler.assembleDependencies(getXRayDependencies());
    }

    private List<XRayDependency> getXRayDependencies() {
        return getProfiles().stream()
                .flatMap(profile -> getRegions(profile).stream().map(region -> fetcher.getServiceGraph(profile, region)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<AwsProfileConfig> getProfiles() {
        return Optional.ofNullable(config.getProfiles()).orElse(List.of());
    }

    private List<String> getRegions(AwsProfileConfig profile) {
        return Optional.ofNullable(profile.getRegions()).orElse(List.of());
    }
}
