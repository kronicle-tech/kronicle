package tech.kronicle.sdk.models.openapi;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.ComponentState;
import tech.kronicle.sdk.models.openapi.OpenApiSpec;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class OpenApiSpecsState implements ComponentState {

    public static final String TYPE = "openapi-specs";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotNull
    List<@Valid OpenApiSpec> openApiSpecs;

    public OpenApiSpecsState(String pluginId, List<OpenApiSpec> openApiSpecs) {
        this.pluginId = pluginId;
        this.openApiSpecs = createUnmodifiableList(openApiSpecs);
    }
}
