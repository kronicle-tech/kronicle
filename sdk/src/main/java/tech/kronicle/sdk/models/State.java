package tech.kronicle.sdk.models;

import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public interface State {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getType();

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getPluginId();

    @Pattern(regexp = PatternStrings.ID)
    String getId();
}
