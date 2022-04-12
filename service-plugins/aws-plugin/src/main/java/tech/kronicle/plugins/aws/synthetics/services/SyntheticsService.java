package tech.kronicle.plugins.aws.synthetics.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.plugins.aws.synthetics.client.SyntheticsClientFacade;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static tech.kronicle.plugins.aws.utils.ProfileUtils.processProfilesToMapEntryList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class SyntheticsService {

    private final SyntheticsClientFacade clientFacade;
    private final TaggedResourceFinder taggedResourceFinder;
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
            return mapCanaryLastRuns(clientFacade.describeCanariesLastRun(profileAndRegion, canaryNames));
        };
    }

    private List<CheckState> mapCanaryLastRuns(List<SyntheticsCanaryLastRun> canaryLastRuns) {
        return canaryLastRuns.stream()
                .map(this::mapCanaryLastRun)
                .collect(toUnmodifiableList());
    }


    private CheckState mapCanaryLastRun(SyntheticsCanaryLastRun canaryLastRun) {
        return CheckState.builder()
                .name(canaryLastRun.getCanaryName())
                .description("AWS Synthetics Canary Run")
                .status(mapStatus(canaryLastRun))
                .statusMessage(canaryLastRun.getState() + " - "
                        + canaryLastRun.getStateReasonCode() + " - "
                        + canaryLastRun.getStateReason())
                .updateTimestamp(canaryLastRun.getCompletedAt())
                .build();
    }

    private ComponentStateCheckStatus mapStatus(SyntheticsCanaryLastRun canaryLastRun) {
        switch (canaryLastRun.getState()) {
            case "RUNNING":
                return ComponentStateCheckStatus.PENDING;
            case "PASSED":
                return ComponentStateCheckStatus.OK;
            case "FAILED":
                return ComponentStateCheckStatus.CRITICAL;
            default:
                log.warn("Unrecognised Synthetics Canary run state \"{}\"", canaryLastRun.getState());
                return ComponentStateCheckStatus.UNKNOWN;
        }
    }
}
