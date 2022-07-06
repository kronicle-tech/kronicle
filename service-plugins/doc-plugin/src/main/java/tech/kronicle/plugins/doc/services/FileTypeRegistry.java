package tech.kronicle.plugins.doc.services;

import org.apache.commons.io.FilenameUtils;
import tech.kronicle.plugins.doc.models.FileType;
import tech.kronicle.sdk.models.doc.DocFileContentType;

import java.util.List;

public class FileTypeRegistry {

    private static final List<FileType> FILE_TYPES = List.of(
            new FileType(List.of("gif"), "image/gif", DocFileContentType.BINARY),
            new FileType(List.of("jpg", "jpeg"), "image/jpeg", DocFileContentType.BINARY),
            new FileType(List.of("json"), "application/json", DocFileContentType.TEXT),
            new FileType(List.of("md", "markdown"), "text/markdown", DocFileContentType.TEXT),
            new FileType(List.of("png"), "image/png", DocFileContentType.BINARY),
            new FileType(List.of("xml"), "application/xml", DocFileContentType.TEXT),
            new FileType(List.of("yaml"), "application/yaml", DocFileContentType.TEXT)
    );

    public FileType getFileType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return FILE_TYPES.stream()
                .filter(fileType -> fileType.getExtensions().contains(extension))
                .findFirst()
                .orElse(null);
    }
}
