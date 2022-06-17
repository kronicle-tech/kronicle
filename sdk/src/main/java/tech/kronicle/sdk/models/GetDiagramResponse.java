package tech.kronicle.sdk.models;

import lombok.Value;

import javax.validation.Valid;

@Value
public class GetDiagramResponse {

    @Valid
    Diagram diagram;
}
