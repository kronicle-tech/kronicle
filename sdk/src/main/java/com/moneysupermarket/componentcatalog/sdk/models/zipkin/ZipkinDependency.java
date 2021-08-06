package com.moneysupermarket.componentcatalog.sdk.models.zipkin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ZipkinDependency {

    String parent;
    String child;
    Integer callCount;
    Integer errorCount;
}
