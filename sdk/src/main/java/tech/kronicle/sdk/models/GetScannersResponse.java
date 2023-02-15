package tech.kronicle.sdk.models;

import lombok.Value;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value
public class GetScannersResponse {

    @NotNull
    List<@NotNull @Valid Scanner> scanners;
}
