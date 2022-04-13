package tech.kronicle.plugins.aws.synthetics.services;

import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
public class SyntheticsCanaryLastRunMapper {

    public List<CheckState> mapCanaryLastRuns(List<SyntheticsCanaryLastRun> canaryLastRuns) {
        return canaryLastRuns.stream()
                .map(this::mapCanaryLastRun)
                .collect(toUnmodifiableList());
    }

    public CheckState mapCanaryLastRun(SyntheticsCanaryLastRun canaryLastRun) {
        return CheckState.builder()
                .name(canaryLastRun.getCanaryName())
                .description("AWS Synthetics Canary Run")
                .status(mapStatus(canaryLastRun))
                .statusMessage(mapStatusMessage(canaryLastRun))
                .updateTimestamp(canaryLastRun.getCompletedAt())
                .build();
    }

    public ComponentStateCheckStatus mapStatus(SyntheticsCanaryLastRun canaryLastRun) {
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

    public String mapStatusMessage(SyntheticsCanaryLastRun canaryLastRun) {
        return Stream.of(
                canaryLastRun.getState(),
                canaryLastRun.getStateReasonCode(),
                canaryLastRun.getStateReason()
        )
                .filter(Objects::nonNull)
                .filter(value -> !value.isEmpty())
                .collect(joining(" - "));
    }
}
