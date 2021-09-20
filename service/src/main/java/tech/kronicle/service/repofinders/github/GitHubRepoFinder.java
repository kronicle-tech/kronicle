package tech.kronicle.service.repofinders.github;

import lombok.RequiredArgsConstructor;
import tech.kronicle.service.models.ApiRepo;
import tech.kronicle.service.repofinders.RepoFinder;
import tech.kronicle.service.repofinders.github.client.GitHubClient;
import tech.kronicle.service.repofinders.github.config.GitHubConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@tech.kronicle.service.spring.stereotypes.RepoFinder
@RequiredArgsConstructor
public class GitHubRepoFinder extends RepoFinder {

  private final GitHubConfig config;
  private final GitHubClient client;

  @Override
  public List<ApiRepo> getApiRepos() {
    if (isNull(config.getUsers())) {
      return List.of();
    }

    return config.getUsers().stream()
      .map(client::getRepos)
      .flatMap(Collection::stream)
      .distinct()
      .collect(Collectors.toList());
  }
}
