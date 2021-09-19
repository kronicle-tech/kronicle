package tech.kronicle.service.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.service.repofinders.RepoProvider;
import tech.kronicle.service.exceptions.ValidationException;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.models.RepoDirAndGit;
import tech.kronicle.service.services.RepoProviderFinder;
import tech.kronicle.service.services.GitCloner;
import tech.kronicle.service.services.ValidatorService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Repository;
import tech.kronicle.common.utils.StringEscapeUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ComponentMetadataRepository {

    private static final String DEFAULT_COMPONENT_METADATA_PATH = "component-metadata.yaml";

    private final RepoProviderFinder finder;
    private final GitCloner gitCloner;
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
        return finder.getRepoProviders().stream()
                .map(RepoProvider::getApiRepos)
                .flatMap(Collection::stream)
                .filter(this::repoHasComponentMetadataFile)
                .map(this::cloneOrPullRepo)
                .filter(Objects::nonNull)
                .map(this::readComponentMetadataFile)
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
            RepoDirAndGit repoDirAndGit = gitCloner.cloneOrPullRepo(apiRepo.getUrl());
            return new RepoAndRepoDir(apiRepo, repoDirAndGit.getRepoDir());
        } catch (GitAPIException | IOException | URISyntaxException e) {
            logError(apiRepo, e);
            return null;
        }
    }

    private RepoAndYaml readComponentMetadataFile(RepoAndRepoDir repoAndRepoDir) {
        try {
            return new RepoAndYaml(repoAndRepoDir.repo, Files.readString(getComponentMetadataFile(repoAndRepoDir)));
        } catch (IOException e) {
            logError(repoAndRepoDir.repo, e);
            return null;
        }
    }

    private Path getComponentMetadataFile(RepoAndRepoDir repoAndRepoDir) {
        return repoAndRepoDir.repoDir.resolve(DEFAULT_COMPONENT_METADATA_PATH);
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
