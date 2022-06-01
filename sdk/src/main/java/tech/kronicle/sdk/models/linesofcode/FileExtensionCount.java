package tech.kronicle.sdk.models.linesofcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
