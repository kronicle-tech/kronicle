package tech.kronicle.plugins.kubernetes.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@Builder
public class ApiResourceItemContainerStatus {

    String name;
    String stateName;
    LocalDateTime stateStartedAt;
}
