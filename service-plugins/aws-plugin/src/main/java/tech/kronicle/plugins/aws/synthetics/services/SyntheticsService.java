package tech.kronicle.plugins.aws.synthetics.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.plugins.aws.synthetics.client.SyntheticsClientFacade;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SyntheticsService {

    private final SyntheticsClientFacade clientFacade;
    private final TaggedResourceFinder taggedResourceFinder;
    private final SyntheticsCanaryLastRunMapper mapper;
    private final AwsConfig config;
    private ResourceIdsByProfileAndRegionAndComponent resourceIdsByProfileAndRegionAndComponent;

    public void refresh() {
        resourceIdsByProfileAndRegionAndComponent = taggedResourceFinder.getResourceIdsByProfileAndRegionAndComponent(
                ResourceTypes.SYNTHETICS_CANARY
        );
    }

    public List<Map.Entry<AwsProfileAndRegion, List<CheckState>>> getCanaryLastRunsForComponent(
            Component component
    ) {
        return processProfilesToMapEntryList(
                config.getProfiles(),
                getCanaryLastRunsForProfileAndRegionAndComponent(component)
        );
    }

    private Function<AwsProfileAndRegion, List<CheckState>> getCanaryLastRunsForProfileAndRegionAndComponent(
            Component component
    ) {
        return profileAndRegion -> {
            List<String> canaryNames = resourceIdsByProfileAndRegionAndComponent.getResourceIds(
                    profileAndRegion,
                    component
            );
            if (canaryNames.isEmpty()) {
                return List.of();
            }
            return mapper.mapCanaryLastRuns(clientFacade.describeCanariesLastRun(profileAndRegion, canaryNames));
        };
    }
}
