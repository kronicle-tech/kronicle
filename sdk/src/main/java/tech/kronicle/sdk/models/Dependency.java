package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Dependency implements ObjectWithReference {

    String sourceComponentId;
    String targetComponentId;

    public String reference() {
        return sourceComponentId + " to " + targetComponentId;
    }
}
