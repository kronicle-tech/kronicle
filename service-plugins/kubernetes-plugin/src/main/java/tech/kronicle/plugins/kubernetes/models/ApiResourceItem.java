package tech.kronicle.plugins.kubernetes.models;

import lombok.Value;

import java.util.Map;

import static tech.kronicle.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
public class ApiResourceItem {

    String name;
    Map<String, String> annotations;

    public ApiResourceItem(String name, Map<String, String> annotations) {
        this.name = name;
        this.annotations = createUnmodifiableMap(annotations);
    }
}
