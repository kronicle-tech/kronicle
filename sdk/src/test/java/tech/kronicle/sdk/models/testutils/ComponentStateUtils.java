package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.ComponentState;

public final class ComponentStateUtils {

    public static ComponentState createComponentState(int stateNumber) {
        return createComponentState(stateNumber, "test-type-" + stateNumber);
    }

    public static ComponentState createComponentState(int stateNumber, String type) {
        return new FakeComponentState(
                type,
                "test-plugin-id-" + stateNumber
        );
    }

    private ComponentStateUtils() {
    }
}
