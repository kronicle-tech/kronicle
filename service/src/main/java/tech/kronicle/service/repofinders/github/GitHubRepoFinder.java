package tech.kronicle.service.repofinders.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.RepoFinder;
import tech.kronicle.service.repofinders.github.client.GitHubClient;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderConfig;
import tech.kronicle.service.repofinders.github.config.GitHubRepoFinderUserConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@tech.kronicle.service.spring.stereotypes.RepoFinder
@RequiredArgsConstructor
@Slf4j
public class GitHubRepoFinder extends RepoFinder {

  private final GitHubRepoFinderConfig config;
  private final GitHubClient client;

  @Override
  public List<ApiRepo> findApiRepos() {
    List<GitHubRepoFinderUserConfig> users = getUsers();
    log.info("Found {} GitHub users", users.size());
    List<ApiRepo> apiRepos = users.stream()
            .map(client::getRepos)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    log.info("Found {} API repos", apiRepos.size());
    return apiRepos;
  }

  private List<GitHubRepoFinderUserConfig> getUsers() {
    return Optional.ofNullable(config.getUsers()).orElseGet(List::of);
  }
}
