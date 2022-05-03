package tech.kronicle.plugins.aws.synthetics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.plugins.aws.synthetics.models.CheckStateAndContext;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SyntheticsCanaryLastRunMapperTest {

    @Test
    public void mapCanaryLastRunsShouldMapCanaryLastRunsToChecks() {
        // Given
        SyntheticsCanaryLastRun canary1 = createCanaryLastRun(1, "RUNNING");
        SyntheticsCanaryLastRun canary2 = createCanaryLastRun(2, "PASSED");
        SyntheticsCanaryLastRun canary3 = createCanaryLastRun(3, "FAILED");
        SyntheticsCanaryLastRun canary4 = createCanaryLastRun(4, "NOT_REAL_STATE");
        List<SyntheticsCanaryLastRun> canaryLastRuns = List.of(
                canary1,
                canary2,
                canary3,
                canary4
        );
        Map<String, String> canaryNameToEnvironmentIdMap = Map.ofEntries(
                Map.entry(canary1.getCanaryName(), "test-environment-id-2"),
                Map.entry(canary2.getCanaryName(), "test-environment-id-1"),
                Map.entry(canary3.getCanaryName(), "test-environment-id-1"),
                Map.entry(canary4.getCanaryName(), "test-environment-id-2")
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        List<CheckStateAndContext> returnValue = underTest.mapCanaryLastRuns(canaryLastRuns, canaryNameToEnvironmentIdMap);

        // Then
        assertThat(returnValue).containsExactly(
                new CheckStateAndContext(
                        "test-environment-id-2",
                        createCheck(1, "RUNNING", ComponentStateCheckStatus.PENDING)
                ),
                new CheckStateAndContext(
                        "test-environment-id-1",
                        createCheck(2, "PASSED", ComponentStateCheckStatus.OK)
                ),
                new CheckStateAndContext(
                        "test-environment-id-1",
                        createCheck(3, "FAILED", ComponentStateCheckStatus.CRITICAL)
                ),
                new CheckStateAndContext(
                        "test-environment-id-2",
                        createCheck(4, "NOT_REAL_STATE", ComponentStateCheckStatus.UNKNOWN)
                )
        );
    }

    @Test
    public void mapStatusMessageShouldIncludeStateAndStateReasonCodeAndStateReason() {
        // Given
        SyntheticsCanaryLastRun canaryLastRun = createCanaryLastRunWithState(
                "FAILED",
                "test-state-reason",
                "test-state-reason-code"
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        String returnValue = underTest.mapStatusMessage(canaryLastRun);

        // Then
        assertThat(returnValue).isEqualTo("FAILED - test-state-reason-code - test-state-reason");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    public void mapStatusMessageShouldOmitStateReasonCodeIfNotPresent(String value) {
        // Given
        SyntheticsCanaryLastRun canaryLastRun = createCanaryLastRunWithState(
                "FAILED",
                "test-state-reason",
                value
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        String returnValue = underTest.mapStatusMessage(canaryLastRun);

        // Then
        assertThat(returnValue).isEqualTo("FAILED - test-state-reason");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    public void mapStatusMessageShouldOmitStateReasonIfNotPresent(String value) {
        // Given
        SyntheticsCanaryLastRun canaryLastRun = createCanaryLastRunWithState(
                "FAILED",
                value,
                "test-state-reason-code"
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        String returnValue = underTest.mapStatusMessage(canaryLastRun);

        // Then
        assertThat(returnValue).isEqualTo("FAILED - test-state-reason-code");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "")
    public void mapStatusMessageShouldOmitStateReasonCodeAndStateReasonIfNotPresent(String value) {
        // Given
        SyntheticsCanaryLastRun canaryLastRun = createCanaryLastRunWithState(
                "FAILED",
                value,
                value
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        String returnValue = underTest.mapStatusMessage(canaryLastRun);

        // Then
        assertThat(returnValue).isEqualTo("FAILED");
    }

    private SyntheticsCanaryLastRun createCanaryLastRunWithState(
            String state,
            String stateReason,
            String stateReasonCode
    ) {
        return new SyntheticsCanaryLastRun(
                null,
                state,
                stateReason,
                stateReasonCode,
                null
        );
    }

    private SyntheticsCanaryLastRun createCanaryLastRun(int canaryLastRunNumber, String state) {
        return new SyntheticsCanaryLastRun(
                "test-canary-name-" + canaryLastRunNumber,
                state,
                "test-state-reason-" + canaryLastRunNumber,
                "test-state-reason-code-" + canaryLastRunNumber,
                LocalDateTime.of(2000, 1, 1, 0, 0, canaryLastRunNumber)
        );
    }

    private CheckState createCheck(int canaryLastRunNumber, String state, ComponentStateCheckStatus status) {
        return CheckState.builder()
                .name("test-canary-name-" + canaryLastRunNumber)
                .description("AWS Synthetics Canary")
                .status(status)
                .statusMessage(state + " - "
                        + "test-state-reason-code-" + canaryLastRunNumber + " - "
                        + "test-state-reason-" + canaryLastRunNumber)
                .updateTimestamp(LocalDateTime.of(2000, 1, 1, 0, 0, canaryLastRunNumber))
                .build();
    }
}
