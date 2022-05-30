package tech.kronicle.plugins.aws.synthetics.services;

import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.aws.AwsPlugin;
import tech.kronicle.plugins.aws.synthetics.models.SyntheticsCanaryLastRun;
import tech.kronicle.sdk.models.CheckState;
import tech.kronicle.sdk.models.ComponentStateCheckStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
public class SyntheticsCanaryLastRunMapper {

    public List<CheckState> mapCanaryLastRuns(
            List<SyntheticsCanaryLastRun> canaryLastRuns,
            Map<String, String> canaryNameToEnvironmentIdMap
    ) {
        return canaryLastRuns.stream()
                .map(mapCanaryLastRun(canaryNameToEnvironmentIdMap))
                .collect(toUnmodifiableList());
    }

    private Function<SyntheticsCanaryLastRun, CheckState> mapCanaryLastRun(
            Map<String, String> canaryNameToEnvironmentIdMap
    ) {
        return canaryLastRun -> CheckState.builder()
                .environmentId(canaryNameToEnvironmentIdMap.get(canaryLastRun.getCanaryName()))
                .pluginId(AwsPlugin.ID)
                .name(canaryLastRun.getCanaryName())
                .description("AWS Synthetics Canary")
                .status(mapStatus(canaryLastRun))
                .statusMessage(mapStatusMessage(canaryLastRun))
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
