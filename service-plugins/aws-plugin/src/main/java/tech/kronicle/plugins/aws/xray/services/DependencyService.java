package tech.kronicle.plugins.aws.xray.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.xray.models.XRayDependency;
import tech.kronicle.sdk.models.Dependency;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DependencyService {

    private final XRayServiceGraphFetcher fetcher;
    private final DependencyAssembler assembler;
    private final AwsConfig config;

    public List<Map.Entry<AwsProfileAndRegion, List<Dependency>>> getDependencies() {
        return getXRayDependencies().stream()
                .map(entry -> Map.entry(entry.getKey(), assembler.assembleDependencies(entry.getValue())))
                .collect(toUnmodifiableList());
    }

    private List<Map.Entry<AwsProfileAndRegion, List<XRayDependency>>> getXRayDependencies() {
        return processProfilesToMapEntryList(config.getProfiles(), fetcher::getServiceGraph);
    }
}
