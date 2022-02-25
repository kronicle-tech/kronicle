package tech.kronicle.plugins.javaimport.services;

import tech.kronicle.sdk.models.Import;
import tech.kronicle.sdk.models.ImportType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class JavaImportFinder {

    private static final Pattern IMPORT_PATTERN = Pattern.compile("(?:^|\\n)\\s*import\\s+([a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*)\\s*;");

    public List<Import> findImports(String scannerId, String contents) {
        return IMPORT_PATTERN.matcher(contents).results()
                .map(matchResult -> matchResult.group(1))
                .map(name -> new Import(scannerId, ImportType.JAVA, name))
                .collect(Collectors.toList());
    }
}
