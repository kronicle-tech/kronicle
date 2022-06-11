package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Diagram implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String name;
    String description;
    List<@Valid Tag> tags;
    List<@Valid DiagramConnection> connections;

    public Diagram(String id, String name, String description, List<@Valid Tag> tags, List<@Valid DiagramConnection> connections) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = createUnmodifiableList(tags);
        this.connections = createUnmodifiableList(connections);
    }

    @Override
    public String reference() {
        return id;
    }
}
