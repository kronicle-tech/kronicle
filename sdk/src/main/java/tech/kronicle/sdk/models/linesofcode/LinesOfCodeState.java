package tech.kronicle.sdk.models.linesofcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.models.ComponentState;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class LinesOfCodeState implements ComponentState {

    String type = "lines-of-code";
    @NotEmpty
    String pluginId;
    Integer count;
    List<@Valid FileExtensionCount> fileExtensionCounts;
}
