package tech.kronicle.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class LogMessageState {

    String message;
    @NotNull
    @Min(0)
    Long count;
}
