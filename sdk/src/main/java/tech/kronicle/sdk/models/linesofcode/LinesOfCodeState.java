package tech.kronicle.sdk.models.linesofcode;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.ComponentState;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class LinesOfCodeState implements ComponentState {

    public static final String TYPE = "lines-of-code";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    Integer count;
    @NotNull
    List<@Valid FileExtensionCount> fileExtensionCounts;

    public LinesOfCodeState(String pluginId, Integer count, List<@Valid FileExtensionCount> fileExtensionCounts) {
        this.pluginId = pluginId;
        this.count = count;
        this.fileExtensionCounts = createUnmodifiableList(fileExtensionCounts);
    }
}
