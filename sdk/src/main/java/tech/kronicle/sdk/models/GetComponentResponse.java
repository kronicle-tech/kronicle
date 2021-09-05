package tech.kronicle.sdk.models;

import lombok.Value;

import javax.validation.Valid;

@Value
public class GetComponentResponse {

    @Valid
    Component component;
}
