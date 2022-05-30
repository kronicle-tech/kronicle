package tech.kronicle.sdk.models.testutils;

import lombok.Value;
import tech.kronicle.sdk.models.ComponentState;

@Value
public class FakeComponentState implements ComponentState {

    String type;
    String pluginId;
}
