package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class Alias implements ObjectWithId, ObjectWithReference {

    @NotBlank
    String id;
    String description;
    String notes;

    @Override
    public String reference() {
        return id;
    }
}
