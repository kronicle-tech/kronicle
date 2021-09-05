package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
@Jacksonized
public class Scanner implements ObjectWithReference {

    @NotBlank
    String id;
    @NotBlank
    String description;
    String notes;

    @Override
    public String reference() {
        return id;
    }
}
