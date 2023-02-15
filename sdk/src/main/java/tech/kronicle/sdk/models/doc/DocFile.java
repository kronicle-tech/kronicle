package tech.kronicle.sdk.models.doc;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Value
@With
@Builder(toBuilder = true)
@Jacksonized
public class DocFile {

    @NotBlank
    String path;
    @NotBlank
    String mediaType;
    DocFileContentType contentType;
    @NotNull
    String content;
}
