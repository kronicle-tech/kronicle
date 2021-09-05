package tech.kronicle.sdk.models.linesofcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import java.util.List;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class LinesOfCode {

    Integer count;
    List<@Valid FileExtensionCount> fileExtensionCounts;
}
