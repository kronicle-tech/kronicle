package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.models.ApiResource;

public final class ApiResourceUtils {

    public static ApiResource createApiResource() {
        return createApiResource(1);
    }

    public static ApiResource createApiResource(int apiResourceNumber) {
        return new ApiResource(
                "TestKind" + apiResourceNumber,
                "test.group." + apiResourceNumber,
                "v" + apiResourceNumber,
                "test-resource-plural-" + apiResourceNumber);
    }

    private ApiResourceUtils() {
    }
}
