package tech.kronicle.plugins.aws.synthetics.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.TaggedResource;
import tech.kronicle.plugins.aws.models.TaggedResourcesByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.plugins.aws.synthetics.client.SyntheticsClientFacade;
import tech.kronicle.plugins.aws.synthetics.models.CheckStateAndContext;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SyntheticsService {

    private final SyntheticsClientFacade clientFacade;
    private final TaggedResourceFinder taggedResourceFinder;
    private final SyntheticsCanaryLastRunMapper mapper;
    private final AwsConfig config;
    private TaggedResourcesByProfileAndRegionAndComponent taggedResourcesByProfileAndRegionAndComponent;

    public void refresh() {
        taggedResourcesByProfileAndRegionAndComponent = taggedResourceFinder.getTaggedResourcesByProfileAndRegionAndComponent(
                ResourceTypes.SYNTHETICS_CANARY
        );
    }

    public List<CheckStateAndContext> getCanaryLastRunsForComponent(
            Component component
    ) {
        return processProfilesToMapEntryList(
                config.getProfiles(),
                getCanaryLastRunsForProfileAndRegionAndComponent(component)
        )
                .stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private Function<AwsProfileAndRegion, List<CheckStateAndContext>> getCanaryLastRunsForProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            List<TaggedResource> canaries = getCanaries(profileAndRegion, component);
            if (canaries.isEmpty()) {
                return List.of();
            }
            return mapper.mapCanaryLastRuns(
                    clientFacade.describeCanariesLastRun(
                            profileAndRegion,
                            getCanaryNames(canaries)
                    ),
                    getCanaryNameToEnvironmentIdMap(canaries)
            );
        };
    }

    private List<TaggedResource> getCanaries(AwsProfileAndRegion profileAndRegion, Component component) {
        return taggedResourcesByProfileAndRegionAndComponent
                .getTaggedResources(
                        profileAndRegion,
                        component
                )
                .stream()
                .collect(toUnmodifiableList());
    }

    private List<String> getCanaryNames(List<TaggedResource> canaries) {
        return canaries.stream()
                .map(TaggedResource::getResourceId)
                .collect(toUnmodifiableList());
    }

    private Map<String, String> getCanaryNameToEnvironmentIdMap(List<TaggedResource> canaries) {
        return canaries.stream()
                .collect(toMap(TaggedResource::getResourceId, TaggedResource::getEnvironmentId));
    }
}
