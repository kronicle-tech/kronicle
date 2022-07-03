package tech.kronicle.plugins.aws.resourcegroupstaggingapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.DiagramConnection;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class ComponentAndConnection {

    Component component;
    DiagramConnection connection;
}
