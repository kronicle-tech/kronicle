package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Import implements ObjectWithScannerId {

    @NotBlank
    String scannerId;
    @NotNull
    ImportType type;
    @NotBlank
    String name;
}
