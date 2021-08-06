package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.moneysupermarket.componentcatalog.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SummarySubComponentDependencyNode implements ObjectWithComponentId {

    @NotBlank
    String componentId;
    @NotBlank
    String spanName;
    @NotNull
    Map<String, String> tags;

    public SummarySubComponentDependencyNode(String componentId, String spanName, Map<String, String> tags) {
        this.componentId = componentId;
        this.spanName = spanName;
        this.tags = createUnmodifiableMap(tags);
    }
}
