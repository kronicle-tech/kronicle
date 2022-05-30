package tech.kronicle.sdk.models;

import tech.kronicle.sdk.constants.PatternStrings;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public interface ComponentEnvironmentState extends ComponentState {

    @NotEmpty
    @Pattern(regexp = PatternStrings.ID)
    String getEnvironmentId();

}
