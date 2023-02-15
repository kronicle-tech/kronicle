package tech.kronicle.sdk.models.readme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.models.ComponentState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ReadmeState implements ComponentState {

    public static final String TYPE = "readme";

    String type = TYPE;
    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String pluginId;
    String id = null;
    @NotEmpty
    String fileName;
    @NotNull
    String content;
}
