package com.moneysupermarket.componentcatalog.service.tests.models;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

import static com.moneysupermarket.componentcatalog.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
@Builder(toBuilder = true)
public class TestContext {

    Map<String, Component> componentMap;

    public TestContext(Map<String, Component> componentMap) {
        this.componentMap = createUnmodifiableMap(componentMap);
    }
}
