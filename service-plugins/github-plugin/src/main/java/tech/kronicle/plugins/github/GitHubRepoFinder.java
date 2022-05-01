package tech.kronicle.plugins.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.scanners.models.Output;
import tech.kronicle.plugins.github.client.GitHubClient;
import tech.kronicle.plugins.github.config.GitHubConfig;
import tech.kronicle.sdk.models.Repo;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Extension
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class GitHubRepoFinder extends RepoFinder {

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final GitHubConfig config;
    private final GitHubClient client;

    @Override
    public String description() {
        return "Find repositories hosted by GitHub.  ";
    }

    @Override
    public Output<List<Repo>, Void> find(Void ignored) {
        return Output.ofOutput(findRepos(), CACHE_TTL);
    }

    private List<Repo> findRepos() {
        return Stream
                .of(
                        findRepos(config::getAccessTokens, client::getRepos, "personal access tokens"),
                        findRepos(config::getUsers, client::getRepos, "users"),
                        findRepos(config::getOrganizations, client::getRepos, "organizations")
                )
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public <T> List<Repo> findRepos(Supplier<List<T>> configItemSupplier, Function<T, List<Repo>> repoGetter, String pluralConfigItemTypeName) {
        List<T> configItems = getConfigItems(configItemSupplier);
        log.info("Found {} GitHub " + pluralConfigItemTypeName, configItems.size());
        List<Repo> repos = getRepos(configItems, repoGetter);
        log.info("Found {} repos via GitHub " + pluralConfigItemTypeName, repos.size());
        return repos;
    }

    private <T> List<T> getConfigItems(Supplier<List<T>> configItemSupplier) {
        return Optional.ofNullable(configItemSupplier.get()).orElseGet(List::of);
    }

    private <T> List<Repo> getRepos(List<T> configItems, Function<T, List<Repo>> repoGetter) {
        return configItems.stream()
                .map(repoGetter)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
