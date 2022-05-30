package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class DiscoveredState implements ComponentEnvironmentState {

    String type = "discovered";
    String pluginId;
    String environmentId;
}
