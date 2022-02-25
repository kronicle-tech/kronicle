package tech.kronicle.plugins.gitlab;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.gitlab.client.GitLabClient;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitLabRepoFinder extends RepoFinder {

    private final GitLabConfig config;
    private final GitLabClient client;

    @Override
    public List<ApiRepo> findApiRepos() {
        return getHosts().stream()
                .flatMap(host -> Stream.of(
                        findApiRepos(host, host::getAccessTokens, client::getRepos, "access tokens"),
                        findApiRepos(host, host::getUsers, client::getRepos, "users"),
                        findApiRepos(host, host::getGroups, client::getRepos, "groups")))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<GitLabHostConfig> getHosts() {
        return Optional.ofNullable(config.getHosts()).orElseGet(List::of);
    }

    public <T> List<ApiRepo> findApiRepos(
            GitLabHostConfig host,
            Supplier<List<T>> configItemSupplier,
            BiFunction<String, T, List<ApiRepo>> repoGetter,
            String pluralConfigItemTypeName) {
        List<T> configItems = getConfigItems(configItemSupplier);
        log.info("Found {} GitLab " + pluralConfigItemTypeName, configItems.size());
        List<ApiRepo> repos = getRepos(host.getBaseUrl(), configItems, repoGetter);
        log.info("Found {} API repos via GitLab " + pluralConfigItemTypeName, repos.size());
        return repos;
    }

    private <T> List<T> getConfigItems(Supplier<List<T>> configItemSupplier) {
        return Optional.ofNullable(configItemSupplier.get()).orElseGet(List::of);
    }

    private <T> List<ApiRepo> getRepos(
            String baseUrl,
            List<T> configItems,
            BiFunction<String, T, List<ApiRepo>> repoGetter) {
        return configItems.stream()
                .map(configItem -> repoGetter.apply(baseUrl, configItem))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
