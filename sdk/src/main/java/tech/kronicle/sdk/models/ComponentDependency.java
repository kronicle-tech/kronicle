package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ComponentDependency implements ObjectWithReference {

    @NotBlank
    String targetComponentId;
    @Pattern(regexp = PatternStrings.ID)
    String typeId;
    DependencyDirection direction;
    String label;
    String description;

    @Override
    public String reference() {
        return targetComponentId + (nonNull(typeId) ? " of type " + typeId : "");
    }
}
