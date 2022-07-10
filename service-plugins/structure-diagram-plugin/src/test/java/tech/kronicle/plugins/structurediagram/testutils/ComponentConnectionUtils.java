package tech.kronicle.plugins.structurediagram.testutils;

import tech.kronicle.sdk.models.ComponentConnection;

public final class ComponentConnectionUtils {

    public static ComponentConnection createComponentConnection(int componentNumber, int componentConnectionNumber) {
        return createComponentConnection(componentNumber, componentConnectionNumber, null, null);
    }

    public static ComponentConnection createComponentConnection(
            int componentNumber,
            int componentConnectionNumber,
            String type,
            String environmentId
    ) {
        return ComponentConnection.builder()
                .targetComponentId("test-target-component-id-" + componentNumber + "-" + componentConnectionNumber)
                .type(type)
                .environmentId(environmentId)
                .build();
    }

    private ComponentConnectionUtils() {
    }
}
