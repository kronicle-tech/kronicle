package tech.kronicle.sdk.models;

import lombok.Value;

import jakarta.validation.Valid;

@Value
public class GetComponentResponse {

    @Valid
    Component component;
}
