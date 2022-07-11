package tech.kronicle.plugins.structurediagram.testutils;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.ComponentConnection;

import java.util.List;

public final class ComponentUtils {

    public static Component createComponent(int componentNumber) {
        return createComponent(componentNumber, List.of());
    }

    public static Component createComponent(int componentNumber, List<ComponentConnection> connections) {
        return Component.builder()
                .id(createComponentId(componentNumber))
                .connections(connections)
                .build();
    }

    public static String createComponentId(int componentNumber) {
        return "test-component-id-" + componentNumber;
    }

    private ComponentUtils() {
    }
}
