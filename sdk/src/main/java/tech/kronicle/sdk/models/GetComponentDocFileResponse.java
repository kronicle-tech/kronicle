package tech.kronicle.sdk.models;

import lombok.Value;
import tech.kronicle.sdk.models.doc.DocFile;

import jakarta.validation.Valid;

@Value
public class GetComponentDocFileResponse {

    @Valid
    DocFile docFile;
}
