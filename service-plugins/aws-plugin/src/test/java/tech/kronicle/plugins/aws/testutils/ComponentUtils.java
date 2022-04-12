package tech.kronicle.plugins.aws.testutils;

import tech.kronicle.sdk.models.Component;

public final class ComponentUtils {

    public static Component createComponent(int componentNumber) {
        return Component.builder()
                .id(createComponentId(componentNumber))
                .build();
    }

    public static String createComponentId(int componentNumber) {
        return "test-component-id-" + componentNumber;
    }

    private ComponentUtils() {
    }
}
