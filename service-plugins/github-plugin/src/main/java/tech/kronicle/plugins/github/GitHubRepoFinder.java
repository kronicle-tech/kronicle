package tech.kronicle.plugins.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import tech.kronicle.pluginapi.finders.RepoFinder;
import tech.kronicle.pluginapi.finders.models.ApiRepo;
import tech.kronicle.plugins.github.client.GitHubClient;
import tech.kronicle.plugins.github.config.GitHubConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Extension
@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubRepoFinder extends RepoFinder {

  private final GitHubConfig config;
  private final GitHubClient client;

  @Override
  public String description() {
      return "Find repositories hosted by GitHub.  ";
  }

  @Override
  public List<ApiRepo> find(Void ignored) {
    return Stream.of(
            findApiRepos(config::getAccessTokens, client::getRepos, "personal access tokens"),
            findApiRepos(config::getUsers, client::getRepos, "users"),
            findApiRepos(config::getOrganizations, client::getRepos, "organizations"))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
  }

  public <T> List<ApiRepo> findApiRepos(Supplier<List<T>> configItemSupplier, Function<T, List<ApiRepo>> repoGetter, String pluralConfigItemTypeName) {
    List<T> configItems = getConfigItems(configItemSupplier);
    log.info("Found {} GitHub " + pluralConfigItemTypeName, configItems.size());
    List<ApiRepo> repos = getRepos(configItems, repoGetter);
    log.info("Found {} API repos via GitHub " + pluralConfigItemTypeName, repos.size());
    return repos;
  }

  private <T> List<ApiRepo> getRepos(List<T> configItems, Function<T, List<ApiRepo>> repoGetter) {
    return configItems.stream()
            .map(repoGetter)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
  }

  private <T> List<T> getConfigItems(Supplier<List<T>> configItemSupplier) {
    return Optional.ofNullable(configItemSupplier.get()).orElseGet(List::of);
  }
}
