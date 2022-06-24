package tech.kronicle.plugins.doc.models;

import lombok.Value;
import tech.kronicle.sdk.models.doc.DocFileContentType;

import java.util.List;

@Value
public class FileType {

    List<String> extensions;
    String mediaType;
    DocFileContentType contentType;
}
