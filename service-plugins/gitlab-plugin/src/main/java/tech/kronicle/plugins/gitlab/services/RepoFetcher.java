package tech.kronicle.plugins.gitlab.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.plugins.gitlab.client.GitLabClient;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;
import tech.kronicle.plugins.gitlab.models.EnrichedGitLabRepo;
import tech.kronicle.plugins.gitlab.models.api.GitLabJob;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class RepoFetcher {

    private final GitLabConfig config;
    private final GitLabClient client;

    public List<EnrichedGitLabRepo> getRepos() {
        return getHosts().stream()
                .flatMap(host -> Stream.of(
                        findRepos(host, host::getAccessTokens, client::getRepos, "access tokens"),
                        findRepos(host, host::getUsers, client::getRepos, "users"),
                        findRepos(host, host::getGroups, client::getRepos, "groups")))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<GitLabJob> getRepoJobs(EnrichedGitLabRepo repo) {
        return client.getJobs(repo);
    }

    private <T> List<EnrichedGitLabRepo> findRepos(
            GitLabHostConfig host,
            Supplier<List<T>> configItemSupplier,
            BiFunction<String, T, List<EnrichedGitLabRepo>> repoGetter,
            String pluralConfigItemTypeName) {
        List<T> configItems = getConfigItems(configItemSupplier);
        log.info("Found {} GitLab " + pluralConfigItemTypeName, configItems.size());
        List<EnrichedGitLabRepo> repos = getRepos(host.getBaseUrl(), configItems, repoGetter);
        log.info("Found {} API repos via GitLab " + pluralConfigItemTypeName, repos.size());
        return repos;
    }

    private List<GitLabHostConfig> getHosts() {
        return Optional.ofNullable(config.getHosts()).orElseGet(List::of);
    }

    private <T> List<T> getConfigItems(Supplier<List<T>> configItemSupplier) {
        return Optional.ofNullable(configItemSupplier.get()).orElseGet(List::of);
    }

    private <T> List<EnrichedGitLabRepo> getRepos(
            String baseUrl,
            List<T> configItems,
            BiFunction<String, T, List<EnrichedGitLabRepo>> repoGetter) {
        return configItems.stream()
                .map(configItem -> repoGetter.apply(baseUrl, configItem))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
