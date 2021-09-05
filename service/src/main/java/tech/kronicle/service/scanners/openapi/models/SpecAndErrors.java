package tech.kronicle.service.scanners.openapi.models;

import tech.kronicle.sdk.models.ScannerError;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;
import lombok.Value;
import lombok.With;

import java.util.List;

@Value
@With
public class SpecAndErrors {

    OpenApiSpec spec;
    List<ScannerError> errors;
}
