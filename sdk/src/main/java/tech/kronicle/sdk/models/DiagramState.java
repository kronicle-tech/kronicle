package tech.kronicle.sdk.models;

import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public interface DiagramState {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getType();

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getPluginId();

}
