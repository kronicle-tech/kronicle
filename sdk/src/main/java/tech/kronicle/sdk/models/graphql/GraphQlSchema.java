package tech.kronicle.sdk.models.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class GraphQlSchema {

    String url;
    String file;
    String description;
    String schema;
}
