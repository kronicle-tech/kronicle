package tech.kronicle.sdk.models;

import tech.kronicle.sdk.constants.PatternStrings;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public interface ComponentEnvironmentState extends ComponentState {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getEnvironmentId();

}
