package tech.kronicle.plugins.doc.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.plugins.doc.DocPlugin;
import tech.kronicle.plugins.doc.models.FileType;
import tech.kronicle.sdk.models.Doc;
import tech.kronicle.sdk.models.doc.DocFile;
import tech.kronicle.sdk.models.doc.DocState;
import tech.kronicle.utils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DocProcessor {

    private final FileTypeRegistry fileTypeRegistry;
    private final FileUtils fileUtils;

    public List<DocState> processDocs(Path codebaseDir, List<Doc> docs) {
        return docs.stream()
                .map(doc -> processDoc(codebaseDir, doc))
                .collect(toUnmodifiableList());
    }

    private DocState processDoc(Path codebaseDir, Doc doc) {
        return DocState.builder()
                .pluginId(DocPlugin.ID)
                .id(doc.getId())
                .dir(doc.getDir())
                .file(doc.getFile())
                .name(doc.getName())
                .description(doc.getDescription())
                .notes(doc.getNotes())
                .tags(doc.getTags())
                .files(processFiles(codebaseDir, doc))
                .build();
    }

    private List<DocFile> processFiles(Path codebaseDir, Doc doc) {
        if (nonNull(doc.getDir())) {
            return processDir(codebaseDir, doc.getDir());
        } else {
            DocFile docFile = processFile(codebaseDir, doc.getFile());
            return nonNull(docFile) ? List.of(docFile) : List.of();
        }
    }

    private List<DocFile> processDir(Path codebaseDir, String dir) {
        Path dirPath = codebaseDir.resolve(dir);
        if (!fileUtils.fileExists(dirPath)) {
            return List.of();
        }
        return fileUtils.findFiles(dirPath)
                .map(filePath -> processFile(codebaseDir, filePath))
                .filter(Objects::nonNull)
                .collect(toUnmodifiableList());
    }

    private DocFile processFile(Path codebaseDir, String file) {
        Path filePath = codebaseDir.resolve(file);
        if (!fileUtils.fileExists(filePath)) {
            return null;
        }
        return processFile(codebaseDir, filePath);
    }

    private DocFile processFile(Path codebaseDir, Path filePath) {
        FileType fileType = fileTypeRegistry.getFileType(filePath.getFileName().toString());
        if (isNull(fileType)) {
            return null;
        }
        return DocFile.builder()
                .path(codebaseDir.relativize(filePath).toString())
                .mediaType(fileType.getMediaType())
                .contentType(fileType.getContentType())
                .content(fileUtils.readFileContent(filePath))
                .build();
    }
}
