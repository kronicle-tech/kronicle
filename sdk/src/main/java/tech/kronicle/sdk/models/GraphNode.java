package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GraphNode implements ObjectWithComponentId {

    @NotBlank
    String componentId;
    String name;
    List<@Valid Tag> tags;

    public GraphNode(String componentId, String name, List<@Valid Tag> tags) {
        this.componentId = componentId;
        this.name = name;
        this.tags = createUnmodifiableList(tags);
    }
}
