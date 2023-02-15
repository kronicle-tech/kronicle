package tech.kronicle.sdk.models;

import lombok.Value;

import jakarta.validation.Valid;

@Value
public class GetDiagramResponse {

    @Valid
    Diagram diagram;
}
