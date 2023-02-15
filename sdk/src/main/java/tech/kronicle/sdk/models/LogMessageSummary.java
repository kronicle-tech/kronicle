package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class LogMessageSummary {

    String message;
    @NotNull
    @Min(0)
    Long count;
}
