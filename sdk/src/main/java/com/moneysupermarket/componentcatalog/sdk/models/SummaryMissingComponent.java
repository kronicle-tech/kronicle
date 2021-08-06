package com.moneysupermarket.componentcatalog.sdk.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class SummaryMissingComponent {

    @NotBlank
    String id;
    @NotBlank
    String scannerId;
}
