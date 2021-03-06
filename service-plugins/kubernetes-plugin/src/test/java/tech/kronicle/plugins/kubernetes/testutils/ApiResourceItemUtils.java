package tech.kronicle.plugins.kubernetes.testutils;

import tech.kronicle.plugins.kubernetes.models.ApiResourceItem;
import tech.kronicle.plugins.kubernetes.models.ApiResourceItemContainerStatus;

import java.util.List;
import java.util.Map;

public final class ApiResourceItemUtils {

    public static ApiResourceItem createApiResourceItem() {
        return createApiResourceItem(Map.of(), List.of());
    }

    public static ApiResourceItem createApiResourceItem(
            Map<String, String> labels,
            List<ApiResourceItemContainerStatus> containerStatuses
    ) {
        return createApiResourceItem(1, labels, containerStatuses);
    }

    public static ApiResourceItem createApiResourceItem(int apiResourceItemNumber) {
        return createApiResourceItem(apiResourceItemNumber, Map.of(), List.of());
    }

    public static ApiResourceItem createApiResourceItem(
            int apiResourceItemNumber,
            Map<String, String> labels,
            List<ApiResourceItemContainerStatus> containerStatuses
    ) {
        return new ApiResourceItem(
                "Test Name " + apiResourceItemNumber,
                Map.ofEntries(),
                labels,
                containerStatuses
        );
    }

    private ApiResourceItemUtils() {
    }
}
