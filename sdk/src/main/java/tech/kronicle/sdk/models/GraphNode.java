package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class GraphNode implements ObjectWithComponentId {

    @NotBlank
    String componentId;
    String spanName;
    List<@Valid Tag> tags;

    public GraphNode(String componentId, String spanName, List<@Valid Tag> tags) {
        this.componentId = componentId;
        this.spanName = spanName;
        this.tags = createUnmodifiableList(tags);    }
}
