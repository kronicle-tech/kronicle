package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Platform implements ObjectWithId, ObjectWithReference {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String id;
    @NotBlank
    String description;
    String notes;

    @Override
    public String reference() {
        return id;
    }
}
