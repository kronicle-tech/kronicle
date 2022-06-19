package tech.kronicle.sdk.models;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class GetDiagramsResponse {

    @NotNull
    List<@NotNull @Valid Diagram> diagrams;
}
