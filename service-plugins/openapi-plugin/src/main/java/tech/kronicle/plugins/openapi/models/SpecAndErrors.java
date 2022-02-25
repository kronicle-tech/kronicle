package tech.kronicle.plugins.openapi.models;

import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

import java.util.List;

@Value
@With
public class SpecAndErrors {

    OpenApiSpec spec;
    List<ScannerError> errors;
}
