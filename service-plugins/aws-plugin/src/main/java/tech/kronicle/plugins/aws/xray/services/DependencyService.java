package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.List;

import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfiles;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyService {

    private final XRayServiceGraphFetcher fetcher;
    private final DependencyAssembler assembler;
    private final AwsConfig config;

    public List<Dependency> getDependencies() {
        return assembler.assembleDependencies(getXRayDependencies());
    }

    private List<XRayDependency> getXRayDependencies() {
        return processProfiles(config.getProfiles(), fetcher::getServiceGraph, false);
    }
}
