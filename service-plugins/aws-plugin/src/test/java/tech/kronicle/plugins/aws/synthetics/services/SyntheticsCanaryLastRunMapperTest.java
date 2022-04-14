package tech.kronicle.plugins.aws.synthetics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SyntheticsCanaryLastRunMapperTest {

    @Test
    public void mapCanaryLastRunsShouldMapCanaryLastRunsToChecks() {
        // Given
        List<SyntheticsCanaryLastRun> canaryLastRuns = List.of(
                createCanaryLastRun(1, "RUNNING"),
                createCanaryLastRun(2, "PASSED"),
                createCanaryLastRun(3, "FAILED"),
                createCanaryLastRun(4, "NOT_REAL_STATE")
        );
        SyntheticsCanaryLastRunMapper underTest = new SyntheticsCanaryLastRunMapper();

        // When
        List<CheckState> returnValue = underTest.mapCanaryLastRuns(canaryLastRuns);

        // Then
        assertThat(returnValue).containsExactly(
                createCheck(1, "RUNNING", ComponentStateCheckStatus.PENDING),
                createCheck(2, "PASSED", ComponentStateCheckStatus.OK),
                createCheck(3, "FAILED", ComponentStateCheckStatus.CRITICAL),
                createCheck(4, "NOT_REAL_STATE", ComponentStateCheckStatus.UNKNOWN)
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
