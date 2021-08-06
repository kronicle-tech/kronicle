package com.moneysupermarket.componentcatalog.service.scanners.zipkin;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.ComponentDependency;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ComponentDependencyHelper {

    public static Component createComponentWithDependencies(String id, String... targetComponentIds) {
        return Component.builder()
                .id(id)
                .dependencies(Arrays.stream(targetComponentIds)
                        .map(targetComponentId -> ComponentDependency.builder().targetComponentId(targetComponentId).build())
                        .collect(Collectors.toList()))
                .build();
    }

    private ComponentDependencyHelper() {
    }
}
