package tech.kronicle.service.tests.models;

import lombok.Builder;
import lombok.Value;
import tech.kronicle.sdk.models.Component;

import java.util.Map;

import static tech.kronicle.sdk.utils.MapUtils.createUnmodifiableMap;

@Value
@Builder(toBuilder = true)
public class TestContext {

    Map<String, Component> componentMap;

    public TestContext(Map<String, Component> componentMap) {
        this.componentMap = createUnmodifiableMap(componentMap);
    }
}
