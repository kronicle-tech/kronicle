package tech.kronicle.plugins.aws.synthetics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.kronicle.plugins.aws.config.AwsConfig;
import tech.kronicle.plugins.aws.config.AwsProfileConfig;
import tech.kronicle.plugins.aws.config.AwsTagKeysConfig;
import tech.kronicle.plugins.aws.constants.ResourceTypes;
import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;
import tech.kronicle.plugins.aws.models.TaggedResource;
import tech.kronicle.plugins.aws.models.TaggedResourcesByProfileAndRegionAndComponent;
import tech.kronicle.plugins.aws.services.TaggedResourceFinder;
import tech.kronicle.plugins.aws.synthetics.client.SyntheticsClientFacade;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.kronicle.plugins.aws.testutils.AwsProfileAndRegionUtils.createProfile;
import static tech.kronicle.plugins.aws.testutils.ComponentUtils.createComponent;

@ExtendWith(MockitoExtension.class)
public class SyntheticsServiceTest {

    private static final List<String> CANARY_RUN_STATES = List.of(
            "RUNNING",
            "PASSED",
            "FAILED"
    );

    @Mock
    private SyntheticsClientFacade clientFacade;
    @Mock
    private TaggedResourceFinder taggedResourceFinder;

    @Test
    public void getCanaryLastRunsForComponentShouldGetCanaryLastRunsForComponent() {
        // Given
        AwsProfileConfig profile1 = createProfile(1);
        AwsProfileConfig profile2 = createProfile(2);
        SyntheticsService underTest = new SyntheticsService(
                clientFacade,
                taggedResourceFinder,
                new SyntheticsCanaryLastRunMapper(),
                new AwsConfig(
                        List.of(profile1, profile2),
                        null,
                        null,
                        null,
                        null,
                        new AwsTagKeysConfig(null, "component", null, null, null),
                        null,
                        null
                )
        );
        AwsProfileAndRegion profile1AndRegion1 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(0));
        AwsProfileAndRegion profile1AndRegion2 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(1));
        AwsProfileAndRegion profile2AndRegion1 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(0));
        AwsProfileAndRegion profile2AndRegion2 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(1));
        Component component = createComponent(1);
        when(taggedResourceFinder.getTaggedResourcesByProfileAndRegionAndComponent(ResourceTypes.SYNTHETICS_CANARY)).thenReturn(
                new TaggedResourcesByProfileAndRegionAndComponent(List.of(
                        createTaggedResourceMapEntry(profile1AndRegion1, component, List.of(1, 2)),
                        createTaggedResourceMapEntry(profile1AndRegion2, component, List.of()),
                        createTaggedResourceMapEntry(profile2AndRegion1, component, List.of()),
                        createTaggedResourceMapEntry(profile2AndRegion2, component, List.of(3, 4))
                ))
        );

        mockDescribeCanariesLastRun(
                profile1AndRegion1,
                List.of(
                        createTaggedResource(profile1AndRegion1, 1),
                        createTaggedResource(profile1AndRegion1, 2)
                )
        );
        mockDescribeCanariesLastRun(
                profile2AndRegion2,
                List.of(
                        createTaggedResource(profile2AndRegion2, 3),
                        createTaggedResource(profile2AndRegion2, 4)
                )
        );

        // When
        underTest.refresh();
        List<CheckState> returnValue = underTest.getCanaryLastRunsForComponent(component);

        // Then
        assertThat(returnValue).containsExactly(
                CheckState.builder()
                        .environmentId(profile1.getEnvironmentId())
                        .pluginId("aws")
                        .name("test-canary-name-1")
                        .description("AWS Synthetics Canary")
                        .status(ComponentStateCheckStatus.PENDING)
                        .statusMessage("RUNNING - test-state-reason-code-1 - test-state-reason-1")
                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 1))
                        .build(),
                CheckState.builder()
                        .environmentId(profile1.getEnvironmentId())
                        .pluginId("aws")
                        .name("test-canary-name-2")
                        .description("AWS Synthetics Canary")
                        .status(ComponentStateCheckStatus.OK)
                        .statusMessage("PASSED - test-state-reason-code-2 - test-state-reason-2")
                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 2))
                        .build(),
                CheckState.builder()
                        .environmentId(profile2.getEnvironmentId())
                        .pluginId("aws")
                        .name("test-canary-name-3")
                        .description("AWS Synthetics Canary")
                        .status(ComponentStateCheckStatus.PENDING)
                        .statusMessage("RUNNING - test-state-reason-code-1 - test-state-reason-1")
                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 1))
                        .build(),
                CheckState.builder()
                        .environmentId(profile2.getEnvironmentId())
                        .pluginId("aws")
                        .name("test-canary-name-4")
                        .description("AWS Synthetics Canary")
                        .status(ComponentStateCheckStatus.OK)
                        .statusMessage("PASSED - test-state-reason-code-2 - test-state-reason-2")
                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 2))
                        .build()
        );
    }

    private Map.Entry<AwsProfileAndRegion, Map<String, List<TaggedResource>>> createTaggedResourceMapEntry(
            AwsProfileAndRegion profileAndRegion,
            Component component,
            List<Integer> canaryNameNumbers
    ) {
        return Map.entry(
                profileAndRegion,
                Map.of(
                        component.getId(),
                        canaryNameNumbers.stream()
                                .map(canaryNameNumber -> createTaggedResource(profileAndRegion, canaryNameNumber))
                                .collect(toUnmodifiableList())
                )
        );
    }

    private void mockDescribeCanariesLastRun(
            AwsProfileAndRegion profileAndRegion,
            List<TaggedResource> canaries
    ) {
        when(clientFacade.describeCanariesLastRun(profileAndRegion, getCanaryNames(canaries))).thenReturn(
                createCanaryLastRuns(getCanaryNames(canaries))
        );
    }

    private List<String> getCanaryNames(List<TaggedResource> canaries) {
        return canaries.stream()
                .map(TaggedResource::getResourceId)
                .collect(toUnmodifiableList());
    }

    private List<SyntheticsCanaryLastRun> createCanaryLastRuns(List<String> canaryNames) {
        return IntStream.range(0, canaryNames.size())
                .mapToObj(createCanaryLastRun(canaryNames))
                .collect(toUnmodifiableList());
    }

    private IntFunction<SyntheticsCanaryLastRun> createCanaryLastRun(List<String> canaryNames) {
        return canaryIndex -> {
            int canaryNumber = canaryIndex + 1;
            return new SyntheticsCanaryLastRun(
                    canaryNames.get(canaryIndex),
                    CANARY_RUN_STATES.get(canaryIndex % CANARY_RUN_STATES.size()),
                    "test-state-reason-" + canaryNumber,
                    "test-state-reason-code-" + canaryNumber,
                    LocalDateTime.of(2000, 1, 1, 0, 0, canaryNumber)
            );
        };
    }

    private TaggedResource createTaggedResource(AwsProfileAndRegion profileAndRegion, int canaryNameNumber) {
        return new TaggedResource(
                "test-canary-name-" + canaryNameNumber,
                profileAndRegion.getProfile().getEnvironmentId()
        );
    }
}
