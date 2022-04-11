package tech.kronicle.plugins.github.constants;

public final class GitHubApiPaths {

  public static final String AUTHENTICATED_USER_REPOS = "/user/repos";
  public static final String USER_REPOS = "/users/{username}/repos";
  public static final String ORGANIZATION_REPOS = "/orgs/{org}/repos";
  public static final String REPO_ACTIONS_RUNS = "/repos/{owner}/{repo}/actions/runs?branch={branch}";

  private GitHubApiPaths() {
  }
}
