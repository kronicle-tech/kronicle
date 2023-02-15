package tech.kronicle.sdk.models.linesofcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class FileExtensionCount {

    @NotNull
    String fileExtension;
    @NotNull
    @Min(0)
    Integer count;
}
