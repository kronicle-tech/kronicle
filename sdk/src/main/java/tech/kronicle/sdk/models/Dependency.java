package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Dependency implements ObjectWithReference {

    String sourceComponentId;
    String targetComponentId;
    String typeId;
    String label;
    String description;

    public String reference() {
        return sourceComponentId + " to " + targetComponentId +
                (nonNull(typeId) ? " of type " + typeId : "");
    }
}
