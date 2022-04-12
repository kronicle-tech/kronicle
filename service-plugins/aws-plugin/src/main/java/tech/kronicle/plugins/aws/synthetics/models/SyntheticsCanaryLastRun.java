package tech.kronicle.plugins.aws.synthetics.models;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class SyntheticsCanaryLastRun {

    String canaryName;
    String state;
    String stateReason;
    String stateReasonCode;
    LocalDateTime completedAt;
}
