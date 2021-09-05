package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class TestResult {

    @NotBlank
    String testId;
    @NotNull
    TestOutcome outcome;
    @NotNull
    Priority priority;
    String message;
}
