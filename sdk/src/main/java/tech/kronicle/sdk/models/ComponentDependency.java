package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ComponentDependency implements ObjectWithReference {

    @NotBlank
    String targetComponentId;
    DependencyDirection direction;
    String description;

    @Override
    public String reference() {
        return targetComponentId;
    }
}
