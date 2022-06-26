package tech.kronicle.service.services.testutils;

import lombok.Value;
import tech.kronicle.sdk.models.ComponentState;

@Value
public class TestComponentState implements ComponentState {

    String type;
    String pluginId = "test-plugin-id";
    String id;
    String value;

    public TestComponentState(int testComponentStateNumber) {
        this.type = "test-component-state-type-" + testComponentStateNumber;
        this.id = "test-component-id-" + testComponentStateNumber;
        this.value = "test-component-value-" + testComponentStateNumber;
    }
}
