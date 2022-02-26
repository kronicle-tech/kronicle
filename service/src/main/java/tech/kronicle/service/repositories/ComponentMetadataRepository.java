package tech.kronicle.service.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tech.kronicle.common.utils.StringEscapeUtils;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.pluginapi.constants.KronicleMetadataFilePaths;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.pluginapi.git.GitCloner;
import tech.kronicle.pluginutils.utils.FileUtils;
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
                getItems(componentMetadataList, ComponentMetadata::getComponentTypes),
                getItems(componentMetadataList, ComponentMetadata::getPlatforms),
                getItems(componentMetadataList, ComponentMetadata::getAreas),
                getItems(componentMetadataList, ComponentMetadata::getTeams),
                getItems(componentMetadataList, ComponentMetadata::getComponents));
    }

    private <T> List<T> getItems(List<ComponentMetadata> componentMetadataList, Function<ComponentMetadata, List<T>> itemsGetter) {
        return componentMetadataList.stream()
                .map(itemsGetter)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ComponentMetadata> getComponentMetadataList() {
        return repoFinderService.findApiRepos().stream()
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
    }

    private boolean repoHasComponentMetadataFile(ApiRepo repo) {
        return repo.getHasComponentMetadataFile();
    }

    private RepoAndRepoDir cloneOrPullRepo(ApiRepo apiRepo) throws RuntimeException {
        try {
            Path repoDir = gitCloner.cloneOrPullRepo(apiRepo.getUrl());
            return new RepoAndRepoDir(apiRepo, repoDir);
        } catch (Exception ex) {
            logError(apiRepo, ex);
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

    private void logError(ApiRepo repo, Exception e) {
        log.error("Could not read Component Metadata file from repo \"{}\"", StringEscapeUtils.escapeString(repo.getUrl()), e);
    }

    @AllArgsConstructor
    private static class RepoAndRepoDir {

        private final ApiRepo repo;
        private final Path repoDir;
    }

    @AllArgsConstructor
    private static class RepoAndYaml {

        private final ApiRepo repo;
        private final String yaml;
    }

    @AllArgsConstructor
    private static class RepoAndComponentMetadata {

        private final ApiRepo repo;
        private final ComponentMetadata componentMetadata;
    }
}
