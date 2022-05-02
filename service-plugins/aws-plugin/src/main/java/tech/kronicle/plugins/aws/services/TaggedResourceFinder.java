package tech.kronicle.plugins.aws.services;

import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.TaggedResource;
import tech.kronicle.plugins.aws.models.TaggedResourcesByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getOptionalResourceTagValue;
import static tech.kronicle.plugins.aws.resourcegroupstaggingapi.utils.ResourceUtils.getResourceTagValue;
import static tech.kronicle.plugins.aws.utils.ArnAnalyser.analyseArn;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

public class TaggedResourceFinder {

    private final ResourceFetcher resourceFetcher;
    private final AwsConfig config;
    private final String componentTagKey;

    @Inject
    public TaggedResourceFinder(
            ResourceFetcher resourceFetcher,
            AwsConfig config
    ) {
        this.resourceFetcher = resourceFetcher;
        this.config = config;
        componentTagKey = config.getTagKeys().getComponent();
    }

    public TaggedResourcesByProfileAndRegionAndComponent getTaggedResourcesByProfileAndRegionAndComponent(String resourceType) {
        return new TaggedResourcesByProfileAndRegionAndComponent(processProfilesToMapEntryList(
                config.getProfiles(),
                prepareTaggedResourcesForProfileAndRegionAndComponent(resourceType)
        ));
    }

    private Function<AwsProfileAndRegion, Map<String, List<TaggedResource>>> prepareTaggedResourcesForProfileAndRegionAndComponent(
            String resourceType
    ) {
        return profileAndRegion -> mapResources(
                profileAndRegion,
                resourceFetcher.getResources(
                        profileAndRegion,
                        List.of(resourceType),
                        Map.ofEntries(Map.entry(componentTagKey, List.of()))
                )
        );
    }

    private Map<String, List<TaggedResource>> mapResources(
            AwsProfileAndRegion profileAndRegion,
            List<ResourceGroupsTaggingApiResource> resources
    ) {
        return resources.stream()
                .map(resource -> Map.entry(
                        getResourceTagValue(resource, componentTagKey),
                        new TaggedResource(
                                analyseArn(resource.getArn()).getResourceId(),
                                getEnvironmentId(resource, profileAndRegion)
                        )
                ))
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
    }

    private String getEnvironmentId(ResourceGroupsTaggingApiResource resource, AwsProfileAndRegion profileAndRegion) {
        return getOptionalResourceTagValue(resource, config.getTagKeys().getEnvironment())
                .orElse(profileAndRegion.getProfile().getEnvironmentId());
    }
}
