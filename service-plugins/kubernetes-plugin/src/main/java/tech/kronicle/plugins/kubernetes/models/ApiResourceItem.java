package tech.kronicle.plugins.kubernetes.models;

import lombok.Value;

import java.util.List;
import java.util.Map;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;
import static tech.kronicle.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
public class ApiResourceItem {

    String name;
    Map<String, String> annotations;
    List<ApiResourceItemContainerStatus> containerStatuses;

    public ApiResourceItem(
            String name,
            Map<String, String> annotations,
            List<ApiResourceItemContainerStatus> containerStatuses
    ) {
        this.name = name;
        this.annotations = createUnmodifiableMap(annotations);
        this.containerStatuses = createUnmodifiableList(containerStatuses);
    }
}
