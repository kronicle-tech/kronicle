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
import tech.kronicle.plugins.aws.models.ResourceIdsByProfileAndRegionAndComponent;
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
                        new AwsTagKeysConfig("component", null),
                        null
                )
        );
        AwsProfileAndRegion profile1AndRegion1 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(0));
        AwsProfileAndRegion profile1AndRegion2 = new AwsProfileAndRegion(profile1, profile1.getRegions().get(1));
        AwsProfileAndRegion profile2AndRegion1 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(0));
        AwsProfileAndRegion profile2AndRegion2 = new AwsProfileAndRegion(profile2, profile2.getRegions().get(1));
        Component component = createComponent(1);
        when(taggedResourceFinder.getResourceIdsByProfileAndRegionAndComponent(ResourceTypes.SYNTHETICS_CANARY)).thenReturn(
                new ResourceIdsByProfileAndRegionAndComponent(List.of(
                        Map.entry(profile1AndRegion1, createResourceIdsByComponentMap(component, List.of(1, 2))),
                        Map.entry(profile1AndRegion2, createResourceIdsByComponentMap(component, List.of())),
                        Map.entry(profile2AndRegion1, createResourceIdsByComponentMap(component, List.of())),
                        Map.entry(profile2AndRegion2, createResourceIdsByComponentMap(component, List.of(3, 4)))
                ))
        );

        mockDescribeCanariesLastRun(
                profile1AndRegion1,
                List.of(
                        createCanaryName(1),
                        createCanaryName(2)
                )
        );
        mockDescribeCanariesLastRun(
                profile2AndRegion2,
                List.of(
                        createCanaryName(3),
                        createCanaryName(4)
                )
        );

        // When
        underTest.refresh();
        List<Map.Entry<AwsProfileAndRegion, List<CheckState>>> returnValue =
                underTest.getCanaryLastRunsForComponent(component);

        // Then
        assertThat(returnValue).hasSize(4);
        assertThat(returnValue.get(0)).isEqualTo(
                Map.entry(
                        new AwsProfileAndRegion(profile1, profile1AndRegion1.getRegion()),
                        List.of(
                                CheckState.builder()
                                        .name("test-access-key-id-1-test-region-1-1-test-canary-name-1")
                                        .description("AWS Synthetics Canary Run")
                                        .status(ComponentStateCheckStatus.PENDING)
                                        .statusMessage("RUNNING - test-state-reason-code-1 - test-state-reason-1")
                                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 1))
                                        .build(),
                                CheckState.builder()
                                        .name("test-access-key-id-1-test-region-1-1-test-canary-name-2")
                                        .description("AWS Synthetics Canary Run")
                                        .status(ComponentStateCheckStatus.OK)
                                        .statusMessage("PASSED - test-state-reason-code-2 - test-state-reason-2")
                                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 2))
                                        .build()
                        )
                )
        );
        assertThat(returnValue.get(1)).isEqualTo(
                Map.entry(
                        new AwsProfileAndRegion(profile1, profile1AndRegion2.getRegion()),
                        List.of()
                )
        );
        assertThat(returnValue.get(2)).isEqualTo(
                Map.entry(
                        new AwsProfileAndRegion(profile2, profile2AndRegion1.getRegion()),
                        List.of()
                )
        );
        assertThat(returnValue.get(3)).isEqualTo(
                Map.entry(
                        new AwsProfileAndRegion(profile2, profile2AndRegion2.getRegion()),
                        List.of(
                                CheckState.builder()
                                        .name("test-access-key-id-2-test-region-2-2-test-canary-name-3")
                                        .description("AWS Synthetics Canary Run")
                                        .status(ComponentStateCheckStatus.PENDING)
                                        .statusMessage("RUNNING - test-state-reason-code-1 - test-state-reason-1")
                                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 1))
                                        .build(),
                                CheckState.builder()
                                        .name("test-access-key-id-2-test-region-2-2-test-canary-name-4")
                                        .description("AWS Synthetics Canary Run")
                                        .status(ComponentStateCheckStatus.OK)
                                        .statusMessage("PASSED - test-state-reason-code-2 - test-state-reason-2")
                                        .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, 2))
                                        .build()
                        )
                )
        );
    }

    private Map<String, List<String>> createResourceIdsByComponentMap(Component component, List<Integer> canaryNameNumbers) {
        if (canaryNameNumbers.isEmpty()) {
            return Map.of();
        }
        return Map.of(
                component.getId(),
                canaryNameNumbers.stream()
                        .map(this::createCanaryName)
                        .collect(toUnmodifiableList())
        );
    }

    private void mockDescribeCanariesLastRun(
            AwsProfileAndRegion profileAndRegion,
            List<String> canaryNames
    ) {
        when(clientFacade.describeCanariesLastRun(profileAndRegion, canaryNames)).thenReturn(
                createCanaryLastRuns(profileAndRegion, canaryNames)
        );
    }

    private List<SyntheticsCanaryLastRun> createCanaryLastRuns(
            AwsProfileAndRegion profileAndRegion,
            List<String> canaryNames
    ) {
        return IntStream.range(0, canaryNames.size())
                .mapToObj(createCanaryLastRun(profileAndRegion, canaryNames))
                .collect(toUnmodifiableList());
    }

    private IntFunction<SyntheticsCanaryLastRun> createCanaryLastRun(
            AwsProfileAndRegion profileAndRegion,
            List<String> canaryNames) {
        return canaryIndex -> {
            int canaryNumber = canaryIndex + 1;
            return new SyntheticsCanaryLastRun(
                    profileAndRegion.getProfile().getAccessKeyId() + "-"
                        + profileAndRegion.getRegion() + "-"
                        + canaryNames.get(canaryIndex),
                    CANARY_RUN_STATES.get(canaryIndex % CANARY_RUN_STATES.size()),
                    "test-state-reason-" + canaryNumber,
                    "test-state-reason-code-" + canaryNumber,
                    LocalDateTime.of(2000, 1, 1, 0, 0, canaryNumber)
            );
        };
    }

    private String createCanaryName(int canaryNameNumber) {
        return "test-canary-name-" + canaryNameNumber;
    }
}
