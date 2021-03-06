package tech.kronicle.service.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tech.kronicle.common.StringEscapeUtils;
import tech.kronicle.sdk.models.ComponentMetadata;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.service.exceptions.ValidationException;
import tech.kronicle.service.services.RepoFinderService;
import tech.kronicle.service.services.ValidatorService;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ComponentMetadataRepository {

    private final RepoFinderService repoFinderService;
    private final GitCloner gitCloner;
    private final FileUtils fileUtils;
    private final YAMLMapper yamlMapper;
    private final ValidatorService validatorService;

    public ComponentMetadata getComponentMetadata() {
        List<ComponentMetadata> componentMetadataList = getComponentMetadataList();
        return new ComponentMetadata(
                getItems(componentMetadataList, ComponentMetadata::getRepos),
                getItems(componentMetadataList, ComponentMetadata::getComponentTypes),
                getItems(componentMetadataList, ComponentMetadata::getPlatforms),
                getItems(componentMetadataList, ComponentMetadata::getAreas),
                getItems(componentMetadataList, ComponentMetadata::getTeams),
                getItems(componentMetadataList, ComponentMetadata::getComponents),
                getItems(componentMetadataList, ComponentMetadata::getDiagrams));
    }

    private <T> List<T> getItems(List<ComponentMetadata> componentMetadataList, Function<ComponentMetadata, List<T>> itemsGetter) {
        return componentMetadataList.stream()
                .map(itemsGetter)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ComponentMetadata> getComponentMetadataList() {
        List<Repo> repos = repoFinderService.findRepos();
        List<ComponentMetadata> componentMetadataList = repos.stream()
                .filter(this::repoHasComponentMetadataFile)
                .map(this::cloneOrPullRepo)
                .filter(Objects::nonNull)
                .map(this::readKronicleMetadataFile)
                .filter(Objects::nonNull)
                .map(this::readComponentMetadataYaml)
                .filter(Objects::nonNull)
                .map(this::validateComponentMetadata)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        componentMetadataList.add(
                ComponentMetadata.builder()
                        .repos(repos)
                        .build()
        );
        return componentMetadataList;
    }

    private boolean repoHasComponentMetadataFile(Repo repo) {
        return repo.getHasComponentMetadataFile();
    }

    private RepoAndRepoDir cloneOrPullRepo(Repo repo) throws RuntimeException {
        try {
            Path repoDir = gitCloner.cloneOrPullRepo(repo.getUrl());
            return new RepoAndRepoDir(repo, repoDir);
        } catch (Exception e) {
            logError(repo, e);
            return null;
        }
    }

    private RepoAndYaml readKronicleMetadataFile(RepoAndRepoDir repoAndRepoDir) {
        try {
            return new RepoAndYaml(repoAndRepoDir.repo, findAndReadKronicleMetadataFile(repoAndRepoDir));
        } catch (RuntimeException e) {
            logError(repoAndRepoDir.repo, e);
            return null;
        }
    }

    private String findAndReadKronicleMetadataFile(RepoAndRepoDir repoAndRepoDir) {
        Path repoDir = repoAndRepoDir.repoDir;
        Optional<Path> file = KronicleMetadataFilePaths.ALL.stream()
            .map(repoDir::resolve)
            .filter(fileUtils::fileExists)
            .findFirst();
        if (file.isPresent()) {
            return fileUtils.readFileContent(file.get());
        }
        throw new RuntimeException(String.format("Could not find Kronicle metadata file in repo \"%s\"",
            repoAndRepoDir.repo.getUrl()));
    }

    private RepoAndComponentMetadata readComponentMetadataYaml(RepoAndYaml repoAndYaml) {
        try {
            return new RepoAndComponentMetadata(repoAndYaml.repo, yamlMapper.readValue(repoAndYaml.yaml, ComponentMetadata.class));
        } catch (JsonProcessingException e) {
            logError(repoAndYaml.repo, e);
            return null;
        }
    }

    private ComponentMetadata validateComponentMetadata(RepoAndComponentMetadata repoAndComponentMetadata) {
        try {
            validatorService.validate(repoAndComponentMetadata.componentMetadata);
            return repoAndComponentMetadata.componentMetadata;
        } catch (ValidationException e) {
            logError(repoAndComponentMetadata.repo, e);
            return null;
        }
    }

    private void logError(Repo repo, Exception e) {
        log.error("Could not read Component Metadata file from repo \"{}\"", StringEscapeUtils.escapeString(repo.getUrl()), e);
    }

    @AllArgsConstructor
    private static class RepoAndRepoDir {

        private final Repo repo;
        private final Path repoDir;
    }

    @AllArgsConstructor
    private static class RepoAndYaml {

        private final Repo repo;
        private final String yaml;
    }

    @AllArgsConstructor
    private static class RepoAndComponentMetadata {

        private final Repo repo;
        private final ComponentMetadata componentMetadata;
    }
}
