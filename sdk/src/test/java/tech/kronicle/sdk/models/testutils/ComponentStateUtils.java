package tech.kronicle.sdk.models.testutils;

import tech.kronicle.sdk.models.ComponentState;

public final class ComponentStateUtils {

    public static ComponentState createComponentState(int stateNumber) {
        return new FakeComponentState(
                "test-type-" + stateNumber,
                "test-plugin-id-" + stateNumber
        );
    }

    private ComponentStateUtils() {
    }
}
