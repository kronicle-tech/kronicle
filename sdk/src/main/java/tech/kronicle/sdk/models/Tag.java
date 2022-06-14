package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Tag {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String key;
    String value;
}
