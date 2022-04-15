package tech.kronicle.plugins.aws.resourcegroupstaggingapi.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;
import java.util.List;

import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceService {

    private final ResourceFetcher fetcher;
    private final ResourceMapper mapper;
    private final AwsConfig config;

    public List<Component> getComponents() {
        return getResources();
    }

    private List<Component> getResources() {
        return processProfilesToList(
                config.getProfiles(),
                profileAndRegion ->  mapper.mapResourcesToComponents(
                        profileAndRegion.getProfile().getEnvironmentId(),
                        fetcher.getResources(profileAndRegion)
                )
        );
    }
}
