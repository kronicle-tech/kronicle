package tech.kronicle.sdk.models;

import lombok.Value;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value
public class GetDiagramsResponse {

    @NotNull
    List<@NotNull @Valid Diagram> diagrams;
}
