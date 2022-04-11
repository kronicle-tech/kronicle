package tech.kronicle.plugins.gitlab;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.sdk.models.Repo;
import tech.kronicle.plugins.gitlab.client.GitLabClient;
import tech.kronicle.plugins.gitlab.config.GitLabConfig;
import tech.kronicle.plugins.gitlab.config.GitLabHostConfig;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitLabRepoFinder extends RepoFinder {

    private final GitLabConfig config;
    private final GitLabClient client;

    @Override
    public String description() {
        return "Find repositories hosted by GitLab.  ";
    }

    @Override
    public List<Repo> find(Void ignored) {
        return getHosts().stream()
                .flatMap(host -> Stream.of(
                        findRepos(host, host::getAccessTokens, client::getRepos, "access tokens"),
                        findRepos(host, host::getUsers, client::getRepos, "users"),
                        findRepos(host, host::getGroups, client::getRepos, "groups")))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<GitLabHostConfig> getHosts() {
        return Optional.ofNullable(config.getHosts()).orElseGet(List::of);
    }

    public <T> List<Repo> findRepos(
            GitLabHostConfig host,
            Supplier<List<T>> configItemSupplier,
            BiFunction<String, T, List<Repo>> repoGetter,
            String pluralConfigItemTypeName) {
        List<T> configItems = getConfigItems(configItemSupplier);
        log.info("Found {} GitLab " + pluralConfigItemTypeName, configItems.size());
        List<Repo> repos = getRepos(host.getBaseUrl(), configItems, repoGetter);
        log.info("Found {} API repos via GitLab " + pluralConfigItemTypeName, repos.size());
        return repos;
    }

    private <T> List<T> getConfigItems(Supplier<List<T>> configItemSupplier) {
        return Optional.ofNullable(configItemSupplier.get()).orElseGet(List::of);
    }

    private <T> List<Repo> getRepos(
            String baseUrl,
            List<T> configItems,
            BiFunction<String, T, List<Repo>> repoGetter) {
        return configItems.stream()
                .map(configItem -> repoGetter.apply(baseUrl, configItem))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
