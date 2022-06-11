package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.util.List;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class DiagramConnection implements ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String sourceComponentId;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String targetComponentId;
    @Pattern(regexp = PatternStrings.ID)
    String type;
    String label;
    String description;
    List<@Valid Tag> tags;

    @Override
    public String reference() {
        return sourceComponentId + " ==" + (nonNull(type) ? type : "") + "==> " + targetComponentId;
    }
}
