package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.graphql.GraphQlSchema;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GraphQlSchemasState implements ComponentState {

    public static final String TYPE = "graphql-schemas";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    @NotNull
    List<@Valid GraphQlSchema> graphQlSchemas;

    public GraphQlSchemasState(String pluginId, List<@Valid GraphQlSchema> graphQlSchemas) {
        this.pluginId = pluginId;
        this.graphQlSchemas = createUnmodifiableList(graphQlSchemas);
    }
}
