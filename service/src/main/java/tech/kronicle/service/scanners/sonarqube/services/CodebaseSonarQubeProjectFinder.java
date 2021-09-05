package tech.kronicle.service.scanners.sonarqube.services;

import tech.kronicle.service.scanners.sonarqube.models.Project;
import tech.kronicle.service.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CodebaseSonarQubeProjectFinder {

    private static final String SONAR_WORD_IN_LOWER_CASE = "sonar";

    private final FileUtils fileUtils;

    public List<Project> findProjects(Path codebaseDir, List<Project> projects) {
        return fileUtils.findFileContents(codebaseDir)
                .filter(this::fileContentContainsSonarReference)
                .flatMap(fileContent -> projects.stream().filter(fileContentContainsProjectKey(fileContent)))
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean fileContentContainsSonarReference(FileUtils.FileContent fileContent) {
        return textContainsSonarReference(fileContent.getContent());
    }

    private boolean textContainsSonarReference(String text) {
        return text.toLowerCase().contains(SONAR_WORD_IN_LOWER_CASE);
    }

    private Predicate<Project> fileContentContainsProjectKey(FileUtils.FileContent fileContent) {
        return project -> getFileContentLines(fileContent).anyMatch(line -> textContainsSonarReference(line) && line.contains(project.getKey()));
    }

    private Stream<String> getFileContentLines(FileUtils.FileContent fileContent) {
        return Stream.of(fileContent.getContent().split("\n"));
    }
}
