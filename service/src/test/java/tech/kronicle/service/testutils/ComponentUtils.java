package tech.kronicle.service.testutils;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.RepoReference;

public final class ComponentUtils {

    public static Component createComponent(int componentNumber) {
        return createComponentBuilder(componentNumber).build();
    }

    public static Component createComponent(int componentNumber, String componentId) {
        return createComponentBuilder(componentNumber)
                .id(componentId)
                .build();
    }

    public static Component.ComponentBuilder createComponentBuilder(int componentNumber) {
        return Component.builder()
                .id(createComponentId(componentNumber))
                .name("Test Component Name " + componentNumber)
                .type("test-component-type-id-" + componentNumber)
                .repo(RepoReference.builder().url("https://example.com/example-" + componentNumber + ".git").build());
    }

    public static String createComponentId(int componentNumber) {
        return "test-component-id-" + componentNumber;
    }

    private ComponentUtils() {
    }
}
