package tech.kronicle.sdk.models.linesofcode;

import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Value
public class FileExtensionCount {

    @NotNull
    String fileExtension;
    @NotNull
    @Min(0)
    Integer count;
}
