package tech.kronicle.sdk.models.openapi;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class OpenApiSpec {

    String scannerId;
    String url;
    String file;
    String description;
    @JsonRawValue
    String spec;
}
