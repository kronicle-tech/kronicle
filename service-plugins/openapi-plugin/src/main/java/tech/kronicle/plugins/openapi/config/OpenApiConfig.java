package tech.kronicle.plugins.openapi.config;

import lombok.Value;
import lombok.experimental.NonFinal;

import jakarta.validation.constraints.NotNull;

@Value
public class OpenApiConfig {

    @NotNull
    Boolean scanCodebases;

    public OpenApiConfig(Boolean scanCodebases) {
        this.scanCodebases = scanCodebases;
    }
}
