package tech.kronicle.service.testutils;

import lombok.Value;
import tech.kronicle.sdk.models.DiagramState;

@Value
public class FakeDiagramState implements DiagramState {

    String type;
    String pluginId;
}
