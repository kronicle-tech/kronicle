package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;

import java.util.Map;

public final class ApiResourceItemUtils {

    public static ApiResourceItem createApiResourceItem() {
        return createApiResourceItem(Map.of());
    }

    public static ApiResourceItem createApiResourceItem(Map<String, String> annotations) {
        return createApiResourceItem(1, annotations);
    }

    public static ApiResourceItem createApiResourceItem(int apiResourceItemNumber) {
        return createApiResourceItem(apiResourceItemNumber, Map.of());
    }

    public static ApiResourceItem createApiResourceItem(int apiResourceItemNumber, Map<String, String> annotations) {
        return new ApiResourceItem("Test Name " + apiResourceItemNumber, annotations);
    }

    private ApiResourceItemUtils() {
    }
}
