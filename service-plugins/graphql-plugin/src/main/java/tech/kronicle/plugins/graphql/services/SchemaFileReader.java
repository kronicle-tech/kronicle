package tech.kronicle.plugins.graphql.services;

import lombok.RequiredArgsConstructor;
import tech.kronicle.utils.FileUtils;

import javax.inject.Inject;
import java.nio.file.Path;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SchemaFileReader {

    private final FileUtils fileUtils;

    public String readSchemaFile(Path codebaseDir, String file) {
        return fileUtils.readFileContent(codebaseDir.resolve(file));
    }
}
