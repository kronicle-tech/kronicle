package tech.kronicle.sdk.models;

import lombok.Value;
import tech.kronicle.sdk.models.doc.DocFile;

import javax.validation.Valid;

@Value
public class GetComponentDocFileResponse {

    @Valid
    DocFile docFile;
}
