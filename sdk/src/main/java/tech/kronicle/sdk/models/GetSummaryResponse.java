package tech.kronicle.sdk.models;

import lombok.Value;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Value
public class GetSummaryResponse {

    @NotNull
    @Valid
    Summary summary;
}
