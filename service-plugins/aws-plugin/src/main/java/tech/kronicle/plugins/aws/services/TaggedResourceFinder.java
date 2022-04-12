package tech.kronicle.plugins.aws.services;

import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.models.ResourceGroupsTaggingApiResource;
import tech.kronicle.plugins.aws.resourcegroupstaggingapi.services.ResourceFetcher;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
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

    public ResourceIdsByProfileAndRegionAndComponent getResourceIdsByProfileAndRegionAndComponent(String resourceType) {
        return new ResourceIdsByProfileAndRegionAndComponent(processProfilesToMapEntryList(
                config.getProfiles(),
                prepareResourceIdsForProfileAndRegionAndComponent(resourceType)
        ));
    }

    private Function<AwsProfileAndRegion, Map<String, List<String>>> prepareResourceIdsForProfileAndRegionAndComponent(
            String resourceType
    ) {
        return profileAndRegion -> mapResource(resourceFetcher.getResources(
                profileAndRegion,
                List.of(resourceType),
                Map.ofEntries(Map.entry(componentTagKey, List.of()))
        ));
    }

    private Map<String, List<String>> mapResource(List<ResourceGroupsTaggingApiResource> resources) {
        return resources.stream()
                .map(resource -> Map.entry(
                        getResourceTagValue(resource, componentTagKey),
                        analyseArn(resource.getArn()).getResourceId()
                ))
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
    }
}
