package tech.kronicle.plugins.graphql.models;

import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;

import java.util.List;

@Value
@With
public class SchemaAndErrors {

    GraphQlSchema schema;
    List<ScannerError> errors;
}
